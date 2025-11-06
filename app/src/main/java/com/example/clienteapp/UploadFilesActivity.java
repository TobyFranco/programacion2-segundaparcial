package com.example.clienteapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clienteapp.api.ApiService;
import com.example.clienteapp.api.RetrofitClient;
import com.example.clienteapp.database.LogHelper;
import com.google.android.material.textfield.TextInputEditText;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadFilesActivity extends AppCompatActivity {

    private static final int PICK_FILES_REQUEST = 100;

    private TextInputEditText etCiCliente;
    private Button btnSeleccionarArchivos, btnSubirArchivos;
    private TextView tvArchivosSeleccionados;
    private ArrayList<Uri> selectedFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_files);

        inicializarVistas();
        configurarListeners();
    }

    private void inicializarVistas() {
        etCiCliente = findViewById(R.id.etCiCliente);
        btnSeleccionarArchivos = findViewById(R.id.btnSeleccionarArchivos);
        btnSubirArchivos = findViewById(R.id.btnSubirArchivos);
        tvArchivosSeleccionados = findViewById(R.id.tvArchivosSeleccionados);
    }

    private void configurarListeners() {
        btnSeleccionarArchivos.setOnClickListener(v -> seleccionarArchivos());
        btnSubirArchivos.setOnClickListener(v -> comprimirYEnviar());
    }

    private void seleccionarArchivos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(
                Intent.createChooser(intent, "Seleccionar archivos"),
                PICK_FILES_REQUEST
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILES_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedFiles.clear();

            if (data.getClipData() != null) {
                // Múltiples archivos seleccionados
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    selectedFiles.add(fileUri);
                }
            } else if (data.getData() != null) {
                // Un solo archivo seleccionado
                selectedFiles.add(data.getData());
            }

            tvArchivosSeleccionados.setText(selectedFiles.size() + " archivo(s) seleccionado(s)");
        }
    }

    private void comprimirYEnviar() {
        String ci = etCiCliente.getText().toString().trim();

        if (ci.isEmpty()) {
            Toast.makeText(this, "Ingrese el CI del cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFiles.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos un archivo", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubirArchivos.setEnabled(false);
        btnSubirArchivos.setText("Procesando...");

        // Ejecutar compresión en segundo plano
        new Thread(() -> {
            try {
                File zipFile = comprimirArchivos(selectedFiles);

                // Volver al hilo principal para enviar
                runOnUiThread(() -> enviarArchivoZip(ci, zipFile));

            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnSubirArchivos.setEnabled(true);
                    btnSubirArchivos.setText("🚀 Comprimir y Enviar");
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    LogHelper.registrarError(this,
                            "Error comprimiendo archivos: " + e.getMessage(),
                            "UploadFilesActivity");
                });
            }
        }).start();
    }

    private File comprimirArchivos(ArrayList<Uri> uris) throws Exception {
        File zipFile = new File(getCacheDir(), "archivos_" + System.currentTimeMillis() + ".zip");
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));

        byte[] buffer = new byte[1024];

        for (Uri uri : uris) {
            String fileName = getFileName(uri);

            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);

            InputStream is = getContentResolver().openInputStream(uri);
            int length;
            while ((length = is.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            is.close();
            zos.closeEntry();
        }

        zos.close();
        return zipFile;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void enviarArchivoZip(String ci, File zipFile) {
        try {
            RequestBody ciBody = RequestBody.create(MediaType.parse("text/plain"), ci);

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("application/zip"), zipFile);
            MultipartBody.Part archivoZip = MultipartBody.Part.createFormData(
                    "archivo", zipFile.getName(), requestFile);

            ApiService api = RetrofitClient.getApiService();
            Call<ResponseBody> call = api.enviarArchivosZip(ciBody, archivoZip);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    btnSubirArchivos.setEnabled(true);
                    btnSubirArchivos.setText("🚀 Comprimir y Enviar");

                    if (response.isSuccessful()) {
                        Toast.makeText(UploadFilesActivity.this,
                                "✅ Archivos enviados con éxito", Toast.LENGTH_LONG).show();
                        LogHelper.registrarError(UploadFilesActivity.this,
                                "Archivos ZIP enviados para CI: " + ci,
                                "UploadFilesActivity");
                        limpiarFormulario();
                    } else {
                        Toast.makeText(UploadFilesActivity.this,
                                "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }

                    // Eliminar archivo temporal
                    zipFile.delete();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    btnSubirArchivos.setEnabled(true);
                    btnSubirArchivos.setText("🚀 Comprimir y Enviar");
                    Toast.makeText(UploadFilesActivity.this,
                            "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    LogHelper.registrarError(UploadFilesActivity.this,
                            "Error enviando ZIP: " + t.getMessage(),
                            "UploadFilesActivity");
                    zipFile.delete();
                }
            });

        } catch (Exception e) {
            btnSubirArchivos.setEnabled(true);
            btnSubirArchivos.setText("🚀 Comprimir y Enviar");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            LogHelper.registrarError(this,
                    "Exception enviando ZIP: " + e.getMessage(),
                    "UploadFilesActivity");
        }
    }

    private void limpiarFormulario() {
        etCiCliente.setText("");
        selectedFiles.clear();
        tvArchivosSeleccionados.setText("No hay archivos seleccionados");
    }
}