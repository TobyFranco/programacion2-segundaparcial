package com.example.clienteapp;

import android.app.Application;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.clienteapp.worker.SyncLogsWorker;
import java.util.concurrent.TimeUnit;

/**
 * Clase Application personalizada para configurar WorkManager
 * al iniciar la aplicación
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Configurar WorkManager al iniciar la app
        configurarWorkManager();
    }

    /**
     * Configura WorkManager para sincronizar logs cada 15 minutos
     */
    private void configurarWorkManager() {
        // Definir restricciones: solo ejecutar si hay conexión a Internet
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Crear solicitud de trabajo periódico
        // NOTA: El intervalo mínimo permitido por Android es 15 minutos
        PeriodicWorkRequest syncWorkRequest =
                new PeriodicWorkRequest.Builder(
                        SyncLogsWorker.class,   // Worker a ejecutar
                        15,                     // Intervalo (mínimo 15)
                        TimeUnit.MINUTES        // Unidad de tiempo
                )
                        .setConstraints(constraints)
                        .build();

        // Programar el trabajo
        // KEEP: Si ya existe un trabajo con este nombre, mantenerlo
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        "SyncLogsWork",                      // Nombre único del trabajo
                        ExistingPeriodicWorkPolicy.KEEP,     // Política si ya existe
                        syncWorkRequest                      // Solicitud de trabajo
                );
    }
}