package com.example.streekipedia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface BingMapsApi {
    @GET
    Call<String> getBingMapsResponse(@Url String url);
}
