package com.example.clienteapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface LogAppDao {
    @Insert
    void insertLog(LogApp log);

    @Query("SELECT * FROM logs_app ORDER BY id DESC")
    List<LogApp> getAllLogs();

    @Query("DELETE FROM logs_app")
    void deleteAllLogs();
}