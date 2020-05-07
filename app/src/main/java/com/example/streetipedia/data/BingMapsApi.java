package com.example.streetipedia.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

//Interface that create a call for Bing Maps API : https://docs.microsoft.com/en-us/bingmaps/
public interface BingMapsApi {
    @GET
    Call<String> getBingMapsResponse(@Url String url);
}
