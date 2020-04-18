package com.example.myfirstandroidproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikipediaApiImage {

    @GET("api.php")
    Call<String> getWikipediaResponseImage(@Query("action") String action, @Query("pageids") String pageids, @Query("format") String format, @Query("prop") String prop);
//https://en.wikipedia.org/w/api.php?action=query&pageids=2304809&format=json&prop=pageimages
}
