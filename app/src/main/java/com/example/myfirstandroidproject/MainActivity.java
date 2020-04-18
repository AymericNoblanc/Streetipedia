package com.example.myfirstandroidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements ListAdapter.SelectedPage {

    private SwipeRefreshLayout swipeContainer;
    private SearchView searchBar;
    ResultsWikiSearch results;

    private static final String BASE_URL = "https://en.wikipedia.org/w/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makeAPICall("Nelson Mandela");
      
        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        searchBar = findViewById(R.id.searchView);
        searchBar.setSubmitButtonEnabled(true);
        searchBar.setQuery("",false);

        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setIconified(false);
            }
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                makeAPICall(query);
                searchBar.setIconified(true);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    
    }

    private void showList(ResultsWikiSearch results) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // define an adapter
        ListAdapter mAdapter = new ListAdapter(results.getSearch(), this);
        recyclerView.setAdapter(mAdapter);


    }

    public void makeAPICall(String search){

        Call<RestWikipediaResponseSearch> call = callRestApiWikipedia(search);
        call.enqueue(new Callback<RestWikipediaResponseSearch>() {
            @Override
            public void onResponse(@NonNull Call<RestWikipediaResponseSearch> call, @NonNull Response<RestWikipediaResponseSearch> response) {
                if(response.isSuccessful() && response.body() != null){
                    results = response.body().getQuery();
                    showList(results);
                    //Toast.makeText(getApplicationContext(), "API Success", Toast.LENGTH_SHORT).show();
                }else{
                    showError();
                }
            }
            @Override
            public void onFailure(@NonNull Call<RestWikipediaResponseSearch> call, @NonNull Throwable t) {
                showError();
            }
        });
    }

    private void showError() {

        Toast.makeText(this, "API Error", Toast.LENGTH_SHORT).show();
    }

    private Call<RestWikipediaResponseSearch> callRestApiWikipedia(String search) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        WikipediaApiSearch WikipediaApi = retrofit.create(WikipediaApiSearch.class);

        return WikipediaApi.getWikipediaResponse("query", "25", "snippet", "search", search, "", "json");

    }

    public void refresh() {
        makeAPICall("Nelson Mandela");
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void selectedPage(ResultWikiSearch result) {
        startActivity(new Intent(MainActivity.this, SelectedPageActivity.class).putExtra("data", result));
    }
}
