package com.example.streetipedia;

import com.example.streetipedia.data.BingMapsApi;
import com.example.streetipedia.data.WikipediaApiImage;
import com.example.streetipedia.data.WikipediaApiInfo;
import com.example.streetipedia.data.WikipediaApiSearch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Singletons {
    //J'ai pas eu la flemme de renommer la classe ;-)

    private static Gson gsonInstance;
    private static BingMapsApi bingMapsApiInstance;
    private static WikipediaApiImage wikipediaApiImageInstance;
    private static WikipediaApiInfo wikipediaApiInfoInstance;
    private static WikipediaApiSearch wikipediaApiSearchInstance;

    public static Gson getGson(){
        if(gsonInstance == null){
            gsonInstance = new GsonBuilder()
                    .setLenient()
                    .create();
        }
        return gsonInstance;
    }

    public static BingMapsApi getBingMapsApi(){
        if(bingMapsApiInstance==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_BING_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build();

            bingMapsApiInstance = retrofit.create(BingMapsApi.class);
        }

        return bingMapsApiInstance;
    }

    public static WikipediaApiImage getWikipediaApiImage(){
        if(wikipediaApiImageInstance==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_WIKIPEDIA_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build();

            wikipediaApiImageInstance = retrofit.create(WikipediaApiImage.class);
        }

        return wikipediaApiImageInstance;
    }

    public static WikipediaApiInfo getWikipediaApiInfo(){
        if(wikipediaApiInfoInstance==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_WIKIPEDIA_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build();

            wikipediaApiInfoInstance = retrofit.create(WikipediaApiInfo.class);
        }

        return wikipediaApiInfoInstance;
    }

    public static WikipediaApiSearch getWikipediaApiSearch(){
        if(wikipediaApiSearchInstance==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_WIKIPEDIA_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build();

            wikipediaApiSearchInstance = retrofit.create(WikipediaApiSearch.class);
        }

        return wikipediaApiSearchInstance;
    }

}
