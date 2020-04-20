package com.example.myfirstandroidproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity implements ListAdapter.SelectedPage {

    private SwipeRefreshLayout swipeContainer;
    private SearchView searchBar;
    ResultsWikiSearch results;
    ResultsWikiInfo resultsInfo;

    String url = null;
    String url2 = null;

    List<Rue> infoRues = new ArrayList<>();

    private List<String> listTypeVoie = Arrays.asList("Allée ", "Avenue ", "Av. ", "Boulevard ", "Carrefour ", "Chemin ", "Chaussée ", "Cité ", "Corniche ", "Cours ", "Domaine ",
            "Descente ", "Ecart ", "Esplanade ", "Faubourg ", "Grande Rue ", "Hameau ", "Halle ", "Impasse ", "Lieu-dit ", "Lottissement ", "Marché ", "Montée ", "Passage ","Passerelle ",
            "Place ", "Plaine ", "Plateau ", "Promenade ", "Parvis ", "Quartier ", "Quai ", "Résidence ", "Ruelle ", "Rocade ", "Rond-Point ", "Route ", "Rue ", "Sentier ",
            "Square ", "Terre-Plein ", "Terrasse ", "Traverse ", "Villa ", "Village ");
    private String titre;

    private static final String BASE_URL = "https://fr.wikipedia.org/w/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> nomsRue = Arrays.asList("Rue Jules Vallès", "Avenue du Général De Gaule", "Rue de l'Orme"); //API Bing

        createListRue(nomsRue);

        showList(infoRues);

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

    private void showList(List<Rue> rueList) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // define an adapter
        ListAdapter mAdapter = new ListAdapter(rueList, this);
        recyclerView.setAdapter(mAdapter);


    }

    public void makeAPICallSearch(String search){
        Call<RestWikipediaResponseSearch> call = callRestApiWikipediaSearch(search);
        try{
            results = call.execute().body().getQuery();
        }catch(IOException e){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
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

        return WikipediaApi.getWikipediaResponse("query", "25", "classic","snippet", "search", search, "", "json");

    }

    public void makeAPICallInfo(String search){

        Call<RestWikipediaResponseInfo> call = callRestApiWikipediaInfo(search);
        try{
            resultsInfo = call.execute().body().getQuery();
        }catch(IOException e){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
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

    public void makeAPICallImage(String search){

        Call<String> call = callRestApiWikipediaImage(search);
        try{
            url = call.execute().body();
            if(url.contains("https://upload.wikimedia.org")) {
                url = url.substring(url.indexOf("https://upload.wikimedia.org"));
                url = url.substring(0,url.indexOf("\""));
                url2 = url;
                if(!(url2.contains("svg"))){
                    url2 = url2.replace("/thumb", "");
                    url2 = url2.substring(0, url.indexOf(".jpg")-6);
                    url2 = url2.concat(".jpg");
                }
            }else{
                url=null;
                url2=null;
            }
        }catch(IOException e){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    private Call<String> callRestApiWikipediaImage(String search) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        WikipediaApiImage WikipediaApi = retrofit.create(WikipediaApiImage.class);

        return WikipediaApi.getWikipediaResponseImage("query", search, "json", "pageimages");
    }

    public void refresh() {

        //TODO refresh the positon and so the list
        List<String> nomsRue2 = Arrays.asList("Rue Jules Verne", "Chemin du Plume-Vert", "Allée des Hirondelles");

        createListRue(nomsRue2);

        showList(infoRues);

        swipeContainer.setRefreshing(false);
    }

    @Override
    public void selectedPage(Rue result) {
        startActivity(new Intent(MainActivity.this, SelectedPageActivity.class).putExtra("data", result));
    }

    public void createListRue(List<String> nomsRue){

        infoRues.clear();

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

            makeAPICallSearch(newRue.getTitre());
            //newRue.setSnippet(results.getSearch().get(0).getSnippet());

            newRue.setPageId(results.getSearch().get(0).getPageid());

            makeAPICallInfo(Integer.toString(newRue.getPageId()));
            newRue.setDescription(resultsInfo.getPages().get(0).getExtract());

            newRue.setSnippet(newRue.getDescription().substring(0,300));

            makeAPICallImage(Integer.toString(newRue.getPageId()));
            newRue.setThumbnail(url);

            newRue.setImage(url2);

            infoRues.add(newRue);

        }

    }
}
