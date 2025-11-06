package com.example.clienteapp.database;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogHelper {

    public static void registrarError(Context context, String descripcion, String clase) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                LogApp log = new LogApp();
                log.setFechaHora(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()).format(new Date()));
                log.setDescripcionError(descripcion);
                log.setClaseOrigen(clase);

                AppDatabase.getInstance(context)
                        .logAppDao()
                        .insertLog(log);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}