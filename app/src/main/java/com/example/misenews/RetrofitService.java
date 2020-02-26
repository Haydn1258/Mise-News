package com.example.misenews;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("v2/local/geo/transcoord.json")
    Call<Location> getTM(@Query("x")Double x, @Query("y")Double y, @Query("input_coord")String input_coord, @Query("output_coord")String output_coord);

}