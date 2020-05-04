package com.example.streekipedia.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikipediaApiImage {

    @GET("api.php")
    Call<String> getWikipediaResponseImage(@Query("action") String action, @Query("pageids") String pageids, @Query("format") String format, @Query("prop") String prop);
}
