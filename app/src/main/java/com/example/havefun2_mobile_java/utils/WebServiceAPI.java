package com.example.havefun2_mobile_java.utils;

import com.example.havefun2_mobile_java.models.Hotel;

import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WebServiceAPI {
    @POST("/api/hotels/create")
    Call<Object> CreateHotel(@Body RequestBody file);
}
