package com.example.myfirstandroidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements ListAdapter.SelectedPage {

    private SwipeRefreshLayout swipeContainer;
    private SearchView searchBar;
    ResultsWikiSearch results;
    ResultsWikiInfo resultsInfo;

    Rue[] infoRues = new Rue[3];

    private List<String> listTypeVoie = Arrays.asList("Allée ", "Avenue ", "Av. ", "Boulevard ", "Carrefour ", "Chemin ", "Chaussée ", "Cité ", "Corniche ", "Cours ", "Domaine ",
            "Descente ", "Ecart ", "Esplanade ", "Faubourg ", "Grande Rue ", "Hameau ", "Halle ", "Impasse ", "Lieu-dit ", "Lottissement ", "Marché ", "Montée ", "Passage ","Passerelle ",
            "Place ", "Plaine ", "Plateau ", "Promenade ", "Parvis ", "Quartier ", "Quai ", "Résidence ", "Ruelle ", "Rocade ", "Rond-Point ", "Route ", "Rue ", "Sentier ",
            "Square ", "Terre-Plein ", "Terrasse ", "Traverse ", "Villa ", "Village ");
    private String titre;

    private static final String BASE_URL = "https://en.wikipedia.org/w/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> nomsRue = Arrays.asList("Rue Jules Vallès", "Avenue du Général DeGaule", "Rue de l'Orme"); //API Bing

        for(int i=0; i<nomsRue.size(); i++){
            Rue newRue = new Rue();

            newRue.setNomRue(nomsRue.get(i));

            titre = nomsRue.get(i);
            for(int j=0; j<listTypeVoie.size();j++){
                titre = titre.replace(listTypeVoie.get(j), "");
            }
            if(titre.startsWith("du")){
                titre = titre.replaceFirst("du ", "");
            }
            if(titre.startsWith("d'")){
                titre = titre.replaceFirst("d'", "");
            }
            if(titre.startsWith("de la")){
                titre = titre.replaceFirst("de la ", "");
            }
            if(titre.startsWith("de l'")){
                titre = titre.replaceFirst("de l'", "");
            }
            if(titre.startsWith("des")){
                titre = titre.replaceFirst("des ", "");
            }
            if(titre.startsWith("de")){
                titre = titre.replaceFirst("de ", "");
            }
            newRue.setTitre(titre);

            try {
                makeAPICallSearch2(newRue.getTitre());
            } catch (IOException e) {
                e.printStackTrace();
            }
            newRue.setSnippet(results.getSearch().get(0).getSnippet());

            newRue.setPageId(results.getSearch().get(0).getPageid());

            makeAPICallInfo2(Integer.toString(newRue.getPageId()));
            newRue.setDescription(resultsInfo.getPages().get(0).getExtract());

            infoRues[i]= newRue;

            //Toast.makeText(this, newRue.getTitre(), Toast.LENGTH_SHORT).show();

            //infoRues.add(newRue);
        }

        makeAPICallSearch("Nelson Mandela");

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
                makeAPICallSearch(query);
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

    public void makeAPICallSearch2(String search) throws IOException {
        Call<RestWikipediaResponseSearch> call = callRestApiWikipediaSearch(search);
        try{
            results = call.execute().body().getQuery();
        }catch(IOException e){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }


    public void makeAPICallSearch(String search){

        Call<RestWikipediaResponseSearch> call = callRestApiWikipediaSearch(search);
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

    private Call<RestWikipediaResponseSearch> callRestApiWikipediaSearch(String search) {

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

    public void makeAPICallInfo2(String search){

        Call<RestWikipediaResponseInfo> call = callRestApiWikipediaInfo(search);
        try{
            resultsInfo = call.execute().body().getQuery();
        }catch(IOException e){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    public void makeAPICallInfo(String search){

        Call<RestWikipediaResponseInfo> call = callRestApiWikipediaInfo(search);
        call.enqueue(new Callback<RestWikipediaResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<RestWikipediaResponseInfo> call, @NonNull Response<RestWikipediaResponseInfo> response) {
                if(response.isSuccessful() && response.body() != null){
                    resultsInfo = response.body().getQuery();

                }else{
                    showError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RestWikipediaResponseInfo> call, @NonNull Throwable t) {
                showError();
            }
        });
    }

    private Call<RestWikipediaResponseInfo> callRestApiWikipediaInfo(String search) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        WikipediaApiInfo wikipediaApi2 = retrofit.create(WikipediaApiInfo.class);

        return wikipediaApi2.getWikipediaResponse2("query", "extracts", "1", search, "1", "2", "json");

    }

    public void refresh() {
        makeAPICallSearch("Nelson Mandela");
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void selectedPage(ResultWikiSearch result) {
        startActivity(new Intent(MainActivity.this, SelectedPageActivity.class).putExtra("data", result));
    }
}
