package com.example.multipartfileandpicassoandeventbus.config;

import com.example.multipartfileandpicassoandeventbus.model.UploadFileResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RemoteDataService {

    @Multipart
    @POST("/uploadFile")
    Call<UploadFileResponse> uploadFile(@Part MultipartBody.Part file);

    @Multipart
    @POST("/uploadMultipleFiles")
    Call<List<UploadFileResponse>> uploadMultipleFiles(@Part MultipartBody.Part[] file);
}
