package com.example.myfirstandroidproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private SwipeRefreshLayout swipeContainer;
    private Integer nbRefresh = 0;

    private static final String BASE_URL = "https://en.wikipedia.org/w/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makeAPICall();
      
              // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                refresh();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    
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



    public void refresh() {

        List<String> input = new ArrayList<>();

        mAdapter = new ListAdapter(input);
        recyclerView.setAdapter(mAdapter);

        nbRefresh++;

        for (int i = 0; i < 100; i++) {
            input.add("Test " + nbRefresh + " : " + i);
        }

        // define an adapter
        mAdapter = new ListAdapter(input);
        recyclerView.setAdapter(mAdapter);
        // Now we call setRefreshing(false) to signal refresh has finished
        swipeContainer.setRefreshing(false);


    }
}
