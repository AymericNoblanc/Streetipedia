package com.example.streekipedia.data;

import com.example.streekipedia.presentation.model.RestWikipediaResponseSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikipediaApiSearch {

    @GET("api.php")
    Call<RestWikipediaResponseSearch> getWikipediaResponse(@Query("action") String one, @Query("srlimit") String srlimit, @Query("srqiprofile") String srqiprofile, @Query("srprop") String srprop, @Query("list") String two, @Query("srsearch") String three, @Query("utf8") String four, @Query("format") String five);
}
