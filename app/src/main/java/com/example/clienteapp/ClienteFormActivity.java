package com.example.clienteapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.example.clienteapp.api.ApiService;
import com.example.clienteapp.api.RetrofitClient;
import com.example.clienteapp.database.LogHelper;
import com.google.android.material.textfield.TextInputEditText;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClienteFormActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE_1 = 1;
    private static final int REQUEST_IMAGE_CAPTURE_2 = 2;
    private static final int REQUEST_IMAGE_CAPTURE_3 = 3;

    private TextInputEditText etCi, etNombre, etDireccion, etTelefono;
    private Button btnFoto1, btnFoto2, btnFoto3, btnEnviar;
    private ImageView ivFoto1, ivFoto2, ivFoto3;

    private File photoFile1, photoFile2, photoFile3;
    private Uri photoUri1, photoUri2, photoUri3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_form);

        inicializarVistas();
        configurarListeners();
        verificarPermisos();
    }

    private void inicializarVistas() {
        etCi = findViewById(R.id.etCi);
        etNombre = findViewById(R.id.etNombre);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);

        btnFoto1 = findViewById(R.id.btnFoto1);
        btnFoto2 = findViewById(R.id.btnFoto2);
        btnFoto3 = findViewById(R.id.btnFoto3);
        btnEnviar = findViewById(R.id.btnEnviar);

        ivFoto1 = findViewById(R.id.ivFoto1);
        ivFoto2 = findViewById(R.id.ivFoto2);
        ivFoto3 = findViewById(R.id.ivFoto3);
    }

    private void configurarListeners() {
        btnFoto1.setOnClickListener(v -> tomarFoto(REQUEST_IMAGE_CAPTURE_1));
        btnFoto2.setOnClickListener(v -> tomarFoto(REQUEST_IMAGE_CAPTURE_2));
        btnFoto3.setOnClickListener(v -> tomarFoto(REQUEST_IMAGE_CAPTURE_3));
        btnEnviar.setOnClickListener(v -> enviarCliente());
    }

    private void verificarPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void tomarFoto(int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                LogHelper.registrarError(this,
                        "Error creando archivo: " + ex.getMessage(),
                        "ClienteFormActivity");
                Toast.makeText(this, "Error al crear archivo", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider", photoFile);

                switch(requestCode) {
                    case REQUEST_IMAGE_CAPTURE_1:
                        photoFile1 = photoFile;
                        photoUri1 = photoUri;
                        break;
                    case REQUEST_IMAGE_CAPTURE_2:
                        photoFile2 = photoFile;
                        photoUri2 = photoUri;
                        break;
                    case REQUEST_IMAGE_CAPTURE_3:
                        photoFile3 = photoFile;
                        photoUri3 = photoUri;
                        break;
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, requestCode);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE_1:
                    if (photoFile1 != null && photoFile1.exists()) {
                        Glide.with(this).load(photoFile1).into(ivFoto1);
                        comprimirImagen(photoFile1);
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE_2:
                    if (photoFile2 != null && photoFile2.exists()) {
                        Glide.with(this).load(photoFile2).into(ivFoto2);
                        comprimirImagen(photoFile2);
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE_3:
                    if (photoFile3 != null && photoFile3.exists()) {
                        Glide.with(this).load(photoFile3).into(ivFoto3);
                        comprimirImagen(photoFile3);
                    }
                    break;
            }
        }
    }

    /**
     * Comprime la imagen para reducir el tamaño del archivo
     */
    private void comprimirImagen(File imageFile) {
        try {
            // Leer la imagen original
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

            if (bitmap != null) {
                // Redimensionar si es muy grande (máximo 1024px en el lado más largo)
                int maxSize = 1024;
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                if (width > maxSize || height > maxSize) {
                    float ratio = Math.min(
                            (float) maxSize / width,
                            (float) maxSize / height
                    );
                    int newWidth = Math.round(width * ratio);
                    int newHeight = Math.round(height * ratio);

                    bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                }

                // Comprimir y guardar (calidad 70%)
                FileOutputStream out = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
                out.flush();
                out.close();
                bitmap.recycle();

                // Mostrar tamaño del archivo
                long fileSizeInKB = imageFile.length() / 1024;
                Toast.makeText(this,
                        "Imagen optimizada: " + fileSizeInKB + " KB",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            LogHelper.registrarError(this,
                    "Error comprimiendo imagen: " + e.getMessage(),
                    "ClienteFormActivity");
        }
    }

    private void enviarCliente() {
        // Validar campos
        String ci = etCi.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        if (ci.isEmpty() || nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoFile1 == null || photoFile2 == null || photoFile3 == null) {
            Toast.makeText(this, "Capture las 3 fotos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEnviar.setEnabled(false);
        btnEnviar.setText("Enviando...");

        try {
            // Crear RequestBody para los campos de texto
            RequestBody ciBody = RequestBody.create(MediaType.parse("text/plain"), ci);
            RequestBody nombreBody = RequestBody.create(MediaType.parse("text/plain"), nombre);
            RequestBody direccionBody = RequestBody.create(MediaType.parse("text/plain"), direccion);
            RequestBody telefonoBody = RequestBody.create(MediaType.parse("text/plain"), telefono);

            // Crear MultipartBody.Part para las fotos
            RequestBody requestFile1 = RequestBody.create(
                    MediaType.parse("image/jpeg"), photoFile1);
            MultipartBody.Part foto1 = MultipartBody.Part.createFormData(
                    "fotoCasa1", photoFile1.getName(), requestFile1);

            RequestBody requestFile2 = RequestBody.create(
                    MediaType.parse("image/jpeg"), photoFile2);
            MultipartBody.Part foto2 = MultipartBody.Part.createFormData(
                    "fotoCasa2", photoFile2.getName(), requestFile2);

            RequestBody requestFile3 = RequestBody.create(
                    MediaType.parse("image/jpeg"), photoFile3);
            MultipartBody.Part foto3 = MultipartBody.Part.createFormData(
                    "fotoCasa3", photoFile3.getName(), requestFile3);

            // Mostrar tamaño total aproximado
            long totalSize = (photoFile1.length() + photoFile2.length() + photoFile3.length()) / 1024;
            Toast.makeText(this, "Enviando " + totalSize + " KB...", Toast.LENGTH_SHORT).show();

            // Enviar con Retrofit
            ApiService api = RetrofitClient.getApiService();
            Call<ResponseBody> call = api.enviarCliente(
                    ciBody, nombreBody, direccionBody, telefonoBody,
                    foto1, foto2, foto3
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    btnEnviar.setEnabled(true);
                    btnEnviar.setText("✅ ENVIAR CLIENTE");

                    if (response.isSuccessful()) {
                        Toast.makeText(ClienteFormActivity.this,
                                "✅ Cliente enviado con éxito", Toast.LENGTH_LONG).show();
                        LogHelper.registrarError(ClienteFormActivity.this,
                                "Cliente enviado: " + ci, "ClienteFormActivity");
                        limpiarFormulario();
                    } else {
                        String errorMsg = "Error " + response.code();
                        if (response.code() == 413) {
                            errorMsg = "Error 413: Imágenes muy grandes. Use fotos más pequeñas.";
                        }
                        Toast.makeText(ClienteFormActivity.this,
                                errorMsg, Toast.LENGTH_LONG).show();
                        LogHelper.registrarError(ClienteFormActivity.this,
                                errorMsg, "ClienteFormActivity");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    btnEnviar.setEnabled(true);
                    btnEnviar.setText("✅ ENVIAR CLIENTE");
                    Toast.makeText(ClienteFormActivity.this,
                            "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    LogHelper.registrarError(ClienteFormActivity.this,
                            "Error enviando cliente: " + t.getMessage(),
                            "ClienteFormActivity");
                }
            });

        } catch (Exception e) {
            btnEnviar.setEnabled(true);
            btnEnviar.setText("✅ ENVIAR CLIENTE");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            LogHelper.registrarError(this, "Exception: " + e.getMessage(),
                    "ClienteFormActivity");
        }
    }

    private void limpiarFormulario() {
        etCi.setText("");
        etNombre.setText("");
        etDireccion.setText("");
        etTelefono.setText("");
        ivFoto1.setImageResource(0);
        ivFoto2.setImageResource(0);
        ivFoto3.setImageResource(0);
        photoFile1 = null;
        photoFile2 = null;
        photoFile3 = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de cámara concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}