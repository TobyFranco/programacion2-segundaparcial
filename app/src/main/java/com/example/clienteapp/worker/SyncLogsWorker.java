package com.example.clienteapp.worker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.clienteapp.api.ApiService;
import com.example.clienteapp.api.RetrofitClient;
import com.example.clienteapp.database.AppDatabase;
import com.example.clienteapp.database.LogApp;
import com.google.gson.Gson;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Worker que sincroniza los logs locales con el servidor cada 15 minutos
 */
public class SyncLogsWorker extends Worker {

    public SyncLogsWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Obtener instancia de la base de datos
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());

            // Obtener todos los logs de la tabla
            List<LogApp> logs = db.logAppDao().getAllLogs();

            // Si no hay logs, no hacer nada
            if (logs.isEmpty()) {
                return Result.success();
            }

            // Convertir lista de logs a JSON
            Gson gson = new Gson();
            String jsonLogs = gson.toJson(logs);

            // Crear RequestBody con el JSON
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    jsonLogs
            );

            // Enviar logs al servidor usando Retrofit
            ApiService api = RetrofitClient.getApiService();
            Response<ResponseBody> response = api.enviarLogs(body).execute();

            if (response.isSuccessful()) {
                // Si el envío fue exitoso, eliminar los logs de la base de datos
                db.logAppDao().deleteAllLogs();

                // Retornar éxito
                return Result.success();
            } else {
                // Si hubo un error en el servidor, reintentar más tarde
                return Result.retry();
            }

        } catch (Exception e) {
            e.printStackTrace();
            // En caso de excepción, reintentar más tarde
            return Result.retry();
        }
    }
}