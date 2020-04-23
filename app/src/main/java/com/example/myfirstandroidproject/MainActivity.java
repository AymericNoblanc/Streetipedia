package com.example.myfirstandroidproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity implements ListAdapter.SelectedPage, LocationListener {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    String lat;
    String provider;
    String latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    String coordinate;

    boolean getGPSlocation;

    private SwipeRefreshLayout swipeContainer;
    ResultsWikiSearch results;
    ResultsWikiInfo resultsInfo;

    String url = null;
    String url2 = null;

    String test = null;

    List<Rue> infoRues = new ArrayList<>();

    Map<Integer, String> hashNomRue = new HashMap<>();
    TreeMap<Integer, String> listNomRue = new TreeMap<>(hashNomRue);
    Integer comptage = 0;

    private List<String> nomsRue;

    private List<String> listTypeVoie = Arrays.asList("Allée ", "Avenue ", "Av. ", "Boulevard ", "Carrefour ", "Chemin ", "Chaussée ", "Cité ", "Corniche ", "Cours ", "Domaine ",
            "Descente ", "Ecart ", "Esplanade ", "Faubourg ", "Grande Rue ", "Hameau ", "Halle ", "Impasse ", "Lieu-dit ", "Lottissement ", "Marché ", "Montée ", "Passage ", "Passerelle ",
            "Place ", "Plaine ", "Plateau ", "Promenade ", "Parvis ", "Quartier ", "Quai ", "Résidence ", "Ruelle ", "Rocade ", "Rond-Point ", "Route ", "Rue ", "Sentier ",
            "Square ", "Terre-Plein ", "Terrasse ", "Traverse ", "Villa ", "Village ");
    private String titre;

    private static final String BASE_URL = "https://fr.wikipedia.org/w/";
    private static final String BASE_BING_URL = "http://dev.virtualearth.net/REST/v1/Locations/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getGPSlocation = false;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        /*Log.d("----------------------",txtLat);

        nomsRue = Arrays.asList("Rue Jules Vallès", "Avenue du Général De Gaule", "Rue de l'Orme"); //API Bing

        makeBingAPICall("http://dev.virtualearth.net/REST/v1/Locations/48.75777,2.68895?o=json&incl=ciso2&key=AsKDhGrY05ocf_6ajFmtLjPfnPI1MxXFALXyVw9kRNrsDlSmEygCllcwizQbnUuS");

        createListRue(nomsRue);

        showList(infoRues);*/

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

        return WikipediaApi.getWikipediaResponse("query", "1", "classic","snippet", "search", search, "", "json");

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
                    if(url2.contains("jpg")){
                        url2 = url2.replace("/thumb", "");
                        url2 = url2.substring(0, url.indexOf(".jpg")-6);
                        url2 = url2.concat(".jpg");
                    }else if(url2.contains("png")){
                        url2 = url2.replace("/thumb", "");
                        url2 = url2.substring(0, url.indexOf(".png")-6);
                        url2 = url2.concat(".png");
                    }
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

    public void makeBingAPICall(String latitudeVar, String longitudeVar, Integer weight){

        Call<String> call = callBingApi("http://dev.virtualearth.net/REST/v1/Locations/" + latitudeVar + "," +  longitudeVar + "?o=json&incl=ciso2&key=AsKDhGrY05ocf_6ajFmtLjPfnPI1MxXFALXyVw9kRNrsDlSmEygCllcwizQbnUuS");
        try{
            test = call.execute().body();
            if(test.contains("baseStreet")){
                test = test.substring(test.indexOf("baseStreet"));
                test = test.substring(0, test.indexOf("intersectionType")-3);

                String[] rue = test.split(",");

                while(hashNomRue.containsKey(weight)){
                    weight++;
                }

                for(int i=0;i<rue.length;i++){
                    rue[i] = rue[i].substring(rue[i].indexOf(":")+1);
                    rue[i] = rue[i].replace("\"","");
                    //nomsRue.add(rue[i]);
                    if(!hashNomRue.containsValue(rue[i])){
                        hashNomRue.put(weight+i,rue[i]);
                    }else{
                        String set = String.valueOf(hashNomRue.entrySet());
                        set = set.substring(0, set.indexOf(rue[i])-1);
                        if(set.contains(" ")){
                            set = set.substring(set.lastIndexOf(" ")+1);
                        }else{
                            set = set.substring(1);
                        }
                        if(Integer.parseInt(set)>weight+i){
                            hashNomRue.put(weight+i,rue[i]);
                            hashNomRue.remove(Integer.parseInt(set));
                        }
                    }
                    comptage++;
                    //Log.d("lsdvezfzefz", rue[i]);
                }
            }

            //nomsRue = Arrays.asList(rue);

            //Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    private Call<String> callBingApi(String bingUrl) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_BING_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        BingMapsApi bingApi = retrofit.create(BingMapsApi.class);

        return bingApi.getBingMapsResponse(bingUrl);
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

    @Override
    public void onLocationChanged(Location location) {
        //txtLat = (TextView) findViewById(R.id.textview1);
        //txtLat = "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude();
        locationManager.removeUpdates(this);
        if(!getGPSlocation){
            getGPSlocation = true;
            latitude = Double.toString(location.getLatitude()).substring(0,Double.toString(location.getLatitude()).indexOf(".") + 5);
            longitude = Double.toString(location.getLongitude()).substring(0,Double.toString(location.getLongitude()).indexOf(".") + 5);

            Double Lat = location.getLatitude();
            Double Long = location.getLongitude();

            coordinate = latitude + "," +  longitude;
            Log.d("----------------------", coordinate);

            /*for (int i=0; i<10; i++){
                for(int j=0; j<10; j++){
                    coordinate[i*10]
                }
            }*/

            nomsRue = Arrays.asList("Rue Jules Vallès", "Rue Jean-Baptiste Clément", "Rue Roland Garros", "Allée Louis Blériot", "Rue Santos-Dumont", "Rue du Hameau", "Place Clément Ader", "Allée Louison Bobet", "Rond-Point Amadeus Mozart", "Allée des Colibris", "Allée des Hirondelles"); //API Bing  Allée des Hirondelles

            collectBingApi(Lat, Long);

            nomsRue = new ArrayList<>(listNomRue.values());

            nomsRue = nomsRue.subList(0,20);

            createListRue(nomsRue);

            showList(infoRues);
        }
    }

    public void collectBingApi(Double Lat, Double Long){

        Double latDist = 0.00045;
        Double longDist = 0.00075;

        Lat -= 5 * latDist;
        Long -= 5 * longDist;

       // .substring(0,Double.toString(location.getLongitude()).indexOf(".") + 5);

        for(int i=0;i<11;i++){
            for(int j=0;j<11;j++){
                makeBingAPICall(Double.toString(Lat+(i*latDist)).substring(0,Double.toString(Lat+(i*latDist)).indexOf(".") + 6),Double.toString(Long+(j*longDist)).substring(0,Double.toString(Long+(j*longDist)).indexOf(".") + 6), (int) Math.sqrt(((i-5)*(i-5))+((j-5)*(j-5)))*1000);
            }
        }

        listNomRue.putAll(hashNomRue);

        Log.d("uhijnjk", Integer.toString(comptage));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }
}
