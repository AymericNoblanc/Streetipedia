package com.example.myfirstandroidproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private static final String BASE_URL = "https://en.wikipedia.org/w/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makeAPICall();
    }

    private void showList(Results results) {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

       /* List<String> input = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            //input.add("Test" + i);
            input.add(results.getSearch().get(1).getPageid().toString());
        }*/

        // define an adapter
        mAdapter = new ListAdapter(results.getSearch());
        recyclerView.setAdapter(mAdapter);

    }

    public void makeAPICall(){

        Call<RestWikipediaResponse> call = callRestApiWikipedia("Nelson Mandela");
        call.enqueue(new Callback<RestWikipediaResponse>() {
            @Override
            public void onResponse(Call<RestWikipediaResponse> call, Response<RestWikipediaResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    Results results = response.body().getQuery();
                    showList(results);
                    //Toast.makeText(getApplicationContext(), "API Success", Toast.LENGTH_SHORT).show();
                }else{
                    showError();
                }
            }

            @Override
            public void onFailure(Call<RestWikipediaResponse> call, Throwable t) {
                showError();
            }
        });
    }

    private void showError() {

        Toast.makeText(this, "API Error", Toast.LENGTH_SHORT).show();
    }

    private Call<RestWikipediaResponse> callRestApiWikipedia(String search){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        WikipediaApi WikipediaApi = retrofit.create(WikipediaApi.class);

        return WikipediaApi.getWikipediaResponse("query","25", "snippet","search",search, "","json");
    }
}
