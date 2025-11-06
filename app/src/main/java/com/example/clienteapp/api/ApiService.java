package com.example.clienteapp.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("57676182-19ac-41a4-aeb4-11b586b411cb")
    Call<ResponseBody> enviarCliente(
            @Part("ci") RequestBody ci,
            @Part("nombreCompleto") RequestBody nombre,
            @Part("direccion") RequestBody direccion,
            @Part("telefono") RequestBody telefono,
            @Part MultipartBody.Part fotoCasa1,
            @Part MultipartBody.Part fotoCasa2,
            @Part MultipartBody.Part fotoCasa3
    );

    @Multipart
    @POST("57676182-19ac-41a4-aeb4-11b586b411cb")
    Call<ResponseBody> enviarArchivosZip(
            @Part("ci") RequestBody ci,
            @Part MultipartBody.Part archivoZip
    );

    @POST("57676182-19ac-41a4-aeb4-11b586b411cb")
    Call<ResponseBody> enviarLogs(@Body RequestBody logs);
}