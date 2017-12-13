package com.example.cimon;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.*;

public interface CimonService {

    @GET("signup/register")
    Call<CimonResponse> signup(@Query("email") String email, @Query("uuid") String uuid);

    @GET("signup/verify")
    Call<CimonResponse> verifyToken(@Query("email") String email, @Query("uuid") String uuid, @Query("token") String token);

    @POST("data/location")
    Call<CimonResponse> sendLocation(@Query("email") String email, @Query("uuid") String uuid, @Query("lat") String lat, @Query("long") String lon, @Query("sourcetime") String time, @Query("accuracty") String accuracy);
}
