package com.example.streetipedia.data;

import com.example.streetipedia.presentation.model.RestWikipediaResponseInfo;
import com.example.streetipedia.presentation.model.RestWikipediaResponseSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApisCall {

    //Creation of a call for Bing Maps API : https://docs.microsoft.com/en-us/bingmaps/
    @GET
    Call<String> getBingMapsResponse(@Url String url);

    //Creation of a call for Wikipedia image API : https://www.mediawiki.org/wiki/API:Images
    @GET("api.php")
    Call<String> getWikipediaResponseImage(@Query("action") String action, @Query("pageids") String pageids, @Query("format") String format, @Query("prop") String prop);

    //Creation of a call for Wikipedia information API : https://www.mediawiki.org/wiki/API:Info
    @GET("api.php")
    Call<RestWikipediaResponseInfo> getWikipediaResponseInfo(@Query("action") String action, @Query("prop") String prop, @Query("exlimit") String exlimit, @Query("pageids") String pageids, @Query("explaintext") String explaintext, @Query("formatversion") String formatversion, @Query("format") String format);

    //Creation of a call for Wikipedia search API : https://www.mediawiki.org/wiki/API:Search
    @GET("api.php")
    Call<RestWikipediaResponseSearch> getWikipediaResponseSearch(@Query("action") String one, @Query("srlimit") String srlimit, @Query("srqiprofile") String srqiprofile, @Query("srprop") String srprop, @Query("list") String two, @Query("srsearch") String three, @Query("utf8") String four, @Query("format") String five);

}
