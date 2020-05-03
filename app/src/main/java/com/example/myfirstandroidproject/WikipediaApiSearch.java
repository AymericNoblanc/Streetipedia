package com.example.myfirstandroidproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikipediaApiSearch {

    @GET("api.php"/*?action=query&list=search&srsearch=Nelson%20Mandela&utf8=&format=json"*/)
    Call<RestWikipediaResponseSearch> getWikipediaResponse(@Query("action") String one, @Query("srlimit") String srlimit, @Query("srqiprofile") String srqiprofile,@Query("srprop") String srprop, @Query("list") String two, @Query("srsearch") String three, @Query("utf8") String four, @Query("format") String five);
}
