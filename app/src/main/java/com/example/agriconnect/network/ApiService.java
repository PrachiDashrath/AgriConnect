package com.example.agriconnect.network;

import com.example.agriconnect.models.PredictionRequest;
import com.example.agriconnect.models.PredictionResponse;
import com.example.agriconnect.models.VegetableResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Health check
    @GET("health")
    Call<Map<String, Object>> healthCheck();

    // Get available vegetables
    @GET("vegetables")
    Call<VegetableResponse> getVegetables();

    // Get districts for a vegetable
    @GET("districts/{vegetable}")
    Call<Map<String, Object>> getDistricts(@Path("vegetable") String vegetable);

    // Predict demand - Make sure this matches exactly
    @POST("predict/demand")
    Call<PredictionResponse> predictDemand(@Body PredictionRequest request);

    // Predict price
    @POST("predict/price")
    Call<PredictionResponse> predictPrice(@Body PredictionRequest request);

    // Compare demand across districts
    @GET("compare/demand")
    Call<Map<String, Object>> compareDemand(@Query("vegetable") String vegetable,
                                            @Query("top_n") int topN);

    // Get market insights
    @GET("insights/market")
    Call<Map<String, Object>> getMarketInsights(@Query("vegetable") String vegetable,
                                                @Query("district") String district);
}