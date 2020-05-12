package com.example.streetipedia;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.streetipedia.data.ApisCall;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//Class that manage some singletons
public class Singletons {

    private static Gson gsonInstance;
    private static SharedPreferences sharedPreferencesInstance;
    private static ApisCall bingMapsApiInstance;
    private static ApisCall wikipediaApiImageInstance;
    private static ApisCall wikipediaApiInfoInstance;
    private static ApisCall wikipediaApiSearchInstance;

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
    public static ApisCall getBingMapsApi(){
        if(bingMapsApiInstance==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_BING_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build();

            bingMapsApiInstance = retrofit.create(ApisCall.class);
        }

        return bingMapsApiInstance;
    }

    //WikipediaApiImage Singleton
    public static ApisCall getWikipediaApiImage(){
        if(wikipediaApiImageInstance==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_WIKIPEDIA_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build();

            wikipediaApiImageInstance = retrofit.create(ApisCall.class);
        }

        return wikipediaApiImageInstance;
    }

    //WikipediaApiInfo Singleton
    public static ApisCall getWikipediaApiInfo(){
        if(wikipediaApiInfoInstance==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_WIKIPEDIA_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build();

            wikipediaApiInfoInstance = retrofit.create(ApisCall.class);
        }

        return wikipediaApiInfoInstance;
    }

    //WikipediaApiSearch Singleton
    public static ApisCall getWikipediaApiSearch(){
        if(wikipediaApiSearchInstance==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_WIKIPEDIA_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build();

            wikipediaApiSearchInstance = retrofit.create(ApisCall.class);
        }

        return wikipediaApiSearchInstance;
    }

}
