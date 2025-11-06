package com.example.clienteapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "logs_app")
public class LogApp {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "fecha_hora")
    private String fechaHora;

    @ColumnInfo(name = "descripcion_error")
    private String descripcionError;

    @ColumnInfo(name = "clase_origen")
    private String claseOrigen;

    public LogApp() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public String getDescripcionError() { return descripcionError; }
    public void setDescripcionError(String descripcionError) {
        this.descripcionError = descripcionError;
    }

    public String getClaseOrigen() { return claseOrigen; }
    public void setClaseOrigen(String claseOrigen) {
        this.claseOrigen = claseOrigen;
    }
}