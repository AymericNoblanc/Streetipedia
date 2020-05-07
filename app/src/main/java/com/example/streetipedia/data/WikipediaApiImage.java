package com.example.streetipedia.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

//Interface that create a call for Wikipedia image API : https://www.mediawiki.org/wiki/API:Images
public interface WikipediaApiImage {
    @GET("api.php")
    Call<String> getWikipediaResponseImage(@Query("action") String action, @Query("pageids") String pageids, @Query("format") String format, @Query("prop") String prop);
}
