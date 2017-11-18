package com.example.cimon;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.*;


/**
 * Created by afzalhossain on 11/9/17.
 */

public interface CimonService {

    @GET("signup/register")
    Call<CimonResponse> signup(@Query("email") String email, @Query("uuid") String uuid);

    @GET("signup/verify")
    Call<CimonResponse> verifyToken(@Query("email") String email, @Query("uuid") String uuid, @Query("token") String token);
}
