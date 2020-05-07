package com.example.streetipedia;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.streetipedia.data.BingMapsApi;
import com.example.streetipedia.data.WikipediaApiImage;
import com.example.streetipedia.data.WikipediaApiInfo;
import com.example.streetipedia.data.WikipediaApiSearch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//Class that manage some singletons
public class Singletons {

    private static Gson gsonInstance;
    private static SharedPreferences sharedPreferencesInstance;
    private static BingMapsApi bingMapsApiInstance;
    private static WikipediaApiImage wikipediaApiImageInstance;
    private static WikipediaApiInfo wikipediaApiInfoInstance;
    private static WikipediaApiSearch wikipediaApiSearchInstance;

    //Gson Singleton
    public static Gson getGson(){
        if(gsonInstance == null){
            gsonInstance = new GsonBuilder()
                    .setLenient()
                    .create();
        }
        return gsonInstance;
    }

    //SharedPreferences Singleton
    public static SharedPreferences getSharedPreferences(Context context){
        if(sharedPreferencesInstance == null){
            sharedPreferencesInstance = context.getSharedPreferences("sharePreference", Context.MODE_PRIVATE);
        }
        return sharedPreferencesInstance;
    }

    //BingMapsApi Singleton
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

    //WikipediaApiImage Singleton
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

    //WikipediaApiInfo Singleton
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

    //WikipediaApiSearch Singleton
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
