package com.example.streekipedia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikipediaApiInfo {

    @GET("api.php")
    Call<RestWikipediaResponseInfo> getWikipediaResponse2(@Query("action") String action, @Query("prop") String prop, @Query("exlimit") String exlimit, @Query("pageids") String pageids, @Query("explaintext") String explaintext, @Query("formatversion") String formatversion, @Query("format") String format);
}
