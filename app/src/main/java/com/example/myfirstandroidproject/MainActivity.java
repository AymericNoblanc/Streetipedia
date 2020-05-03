package com.example.myfirstandroidproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
    Double Lat, Long;
    protected boolean gps_enabled, network_enabled;
    String coordinate;

    boolean getGPSlocation;
    boolean recyclerViewStatue;

    private SwipeRefreshLayout swipeContainer;
    ResultsWikiSearch results;
    ResultsWikiInfo resultsInfo;

    Location location;

    String url = null;
    String url2 = null;

    String test = null;

    List<Rue> infoRues = new ArrayList<>();

    Map<Integer, String> hashNomRue = new HashMap<>();
    TreeMap<Integer, String> listNomRue = new TreeMap<>(hashNomRue);
    Integer comptage = 0;

    RecyclerView recyclerView;

    boolean GPSrefresh = false;

    private List<String> nomsRue = new ArrayList<>();

    private List<String> listTypeVoie = Arrays.asList("Allée ", "Avenue ", "Av. ", "Boulevard ", "Carrefour ", "Chemin ", "Chaussée ", "Cité ", "Corniche ", "Cours ", "Domaine ",
            "Descente ", "Ecart ", "Esplanade ", "Faubourg ", "Grande Rue ", "Hameau ", "Halle ", "Impasse ", "Lieu-dit ", "Lottissement ", "Marché ", "Montée ", "Passage ", "Passerelle ",
            "Place ", "Plaine ", "Plateau ", "Promenade ", "Parvis ", "Quartier ", "Quai ", "Résidence ", "Ruelle ", "Rocade ", "Rond-Point ", "Route ", "Rue ", "Sentier ",
            "Square ", "Terre-Plein ", "Terrasse ", "Traverse ", "Villa ", "Village ");
    private String titre;

    private static final String BASE_URL = "https://fr.wikipedia.org/w/";
    private static final String BASE_BING_URL = "http://dev.virtualearth.net/REST/v1/Locations/";

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;

    ImageButton reglageButton;

    ConstraintLayout layout;
    ConstraintSet constraintSetNormal = new ConstraintSet();
    ConstraintSet constraintSetReglage = new ConstraintSet();
    boolean reglage = false;

    SeekBar seekBar;
    TextView rapiditeTV;
    TextView quantiteTV;
    TextView surfaceTV;
    TextView pasTV;
    View rectangle;

    int pas=2;
    int surface=1;

    int oldValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (ConstraintLayout) findViewById(R.id.layout);

        constraintSetNormal.clone(layout);
        constraintSetReglage.clone(this, R.layout.activity_main_reglage);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        reglageButton = (ImageButton) findViewById(R.id.imageButton);
        reglageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(layout);

                if(reglage){
                    constraintSetNormal.applyTo(layout);
                    reglage=false;
                }else{
                    constraintSetReglage.applyTo(layout);
                    seekBar.setEnabled(true);
                    reglage=true;
                }
            }
        });

        oldValue=1;

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(0);
        seekBar.incrementProgressBy(1);
        seekBar.setMax(4);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //textView.setText(String.valueOf(progress));
                switch(progress){
                    case 0:
                        surfaceTV.setText("Surface : Position");
                        pasTV.setText("Pas : 0m");
                        surface = 0;
                        pas = 1;
                        break;
                    case 1:
                        surfaceTV.setText("Surface : 100x100");
                        pasTV.setText("Pas : 100m");
                        surface = 1;
                        pas = 2;
                        break;
                    case 2:
                        surfaceTV.setText("Surface : 200x200");
                        pasTV.setText("Pas : 100m");
                        surface = 2;
                        pas = 2;
                        break;
                    case 3:
                        surfaceTV.setText("Surface : 150x150");
                        pasTV.setText("Pas : 50m");
                        surface = 3;
                        pas = 1;
                        break;
                    case 4:
                        surfaceTV.setText("Surface : 250x250");
                        pasTV.setText("Pas : 50m");
                        surface = 5;
                        pas = 1;
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(oldValue<seekBar.getProgress()){
                    if(infoRues.size()!=20){
                        refresh();
                    }
                    oldValue=seekBar.getProgress();
                }

            }
        });

        rapiditeTV = (TextView) findViewById(R.id.rapidite);
        quantiteTV = (TextView) findViewById(R.id.quantite);
        surfaceTV = (TextView) findViewById(R.id.surface);
        pasTV = (TextView) findViewById(R.id.pas);
        rectangle = (View) findViewById(R.id.myRectangleView);

        recyclerViewStatue = false;
        getGPSlocation = false;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            TimeUnit.MILLISECONDS.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Chargement().execute();

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
                oldValue=seekBar.getProgress();
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

        recyclerViewStatue = true;
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

        getGPSlocation = false;

        swipeContainer.setRefreshing(false);

        new Chargement().execute();

    }

    @Override
    public void selectedPage(Rue result) {
        if(recyclerViewStatue){
            startActivity(new Intent(MainActivity.this, SelectedPageActivity.class).putExtra("data", result));
        }
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

            if(i==0){
                newRue.setNomRue(newRue.getNomRue()+"*");
            }

            infoRues.add(newRue);
        }

    }

    public void chargement () {
        if(!getGPSlocation){
            GPSrefresh=true;
            getGPSlocation = true;

            coordinate = latitude + "," +  longitude;
            Log.d("----------------------", coordinate);

            /*for (int i=0; i<10; i++){
                for(int j=0; j<10; j++){
                    coordinate[i*10]
                }
            }*/

            //nomsRue = Arrays.asList("Rue Jules Vallès", "Rue Jean-Baptiste Clément", "Rue Roland Garros", "Allée Louis Blériot", "Rue Santos-Dumont", "Rue du Hameau", "Place Clément Ader", "Allée Louison Bobet", "Rond-Point Amadeus Mozart", "Allée des Colibris", "Allée des Hirondelles"); //API Bing  Allée des Hirondelles


            //A way to wait the GPS Location and not do the Bing API call without location
            try {
                TimeUnit.MILLISECONDS.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            collectBingApi();

            nomsRue = new ArrayList<>(listNomRue.values());

            if(nomsRue.size()>=20){
                nomsRue = nomsRue.subList(0,20);
            }

            createListRue(nomsRue);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //txtLat = (TextView) findViewById(R.id.textview1);
        //txtLat = "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude();
        locationManager.removeUpdates(this);
        /*if(GPSrefresh){
            GPSOnRefresh(location);
        }*/

    }

    public void collectBingApi(){

        listNomRue.clear();
        hashNomRue.clear();

        int distanceMax = surface;

        Double latDist = 0.00045*pas;//50m * le pas
        Double longDist = 0.00075*pas;//50m * le pas

        String stringLat;
        String stringLong;
        int weight;

       if(Lat.toString().length()-Lat.toString().indexOf(".")+1<5){
            Lat += 0.00001;
        }
        Lat = Double.parseDouble(Lat.toString().substring(0,Lat.toString().indexOf(".")+6));

        if(Long.toString().length()-Long.toString().indexOf(".")+1<5){
            Long += 0.00001;
        }
        Long = Double.parseDouble(Long.toString().substring(0,Long.toString().indexOf(".")+6));


        Lat -= distanceMax * latDist;
        Long -= distanceMax * longDist;

       // .substring(0,Double.toString(location.getLongitude()).indexOf(".") + 5);

        for(int i=0;i<distanceMax*2+1;i++){
            for(int j=0;j<distanceMax*2+1;j++){

                if(Double.toString(Lat+(i*latDist)).length()-Double.toString(Lat+(i*latDist)).indexOf(".")+1<5){
                    Lat += 0.00001;
                }
                if(Double.toString(Long+(j*longDist)).length()-Double.toString(Long+(j*longDist)).indexOf(".")+1<5){
                    Long += 0.00001;
                }

                stringLat = Double.toString(Lat+(i*latDist));
                stringLong = Double.toString(Long+(j*longDist));
                weight = (int) (Math.sqrt(((i-distanceMax)*(i-distanceMax))+((j-distanceMax)*(j-distanceMax)))*1000);
                makeBingAPICall(stringLat, stringLong, weight);
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


    private class Chargement extends AsyncTask<Void, Void, Void> implements LocationListener{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView.setEnabled(false);
            recyclerView.setVisibility(View.GONE);
            reglageButton.setEnabled(false);
            reglageButton.setVisibility(View.GONE);
            rapiditeTV.setEnabled(false);
            rapiditeTV.setVisibility(View.INVISIBLE);
            quantiteTV.setEnabled(false);
            quantiteTV.setVisibility(View.INVISIBLE);
            surfaceTV.setEnabled(false);
            surfaceTV.setVisibility(View.INVISIBLE);
            pasTV.setEnabled(false);
            pasTV.setVisibility(View.INVISIBLE);
            rectangle.setEnabled(false);
            rectangle.setVisibility(View.INVISIBLE);
            seekBar.setEnabled(false);
            seekBar.setVisibility(View.INVISIBLE);
            recyclerViewStatue=false;
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            showList(infoRues);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setEnabled(true);
            reglageButton.setVisibility(View.VISIBLE);
            reglageButton.setEnabled(true);
            if(reglage) {
                rapiditeTV.setEnabled(true);
                rapiditeTV.setVisibility(View.VISIBLE);
                quantiteTV.setEnabled(true);
                quantiteTV.setVisibility(View.VISIBLE);
                surfaceTV.setEnabled(true);
                surfaceTV.setVisibility(View.VISIBLE);
                pasTV.setEnabled(true);
                pasTV.setVisibility(View.VISIBLE);
                rectangle.setEnabled(true);
                rectangle.setVisibility(View.VISIBLE);
                seekBar.setEnabled(true);
                seekBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            chargement();
            return null;
        }

        @Override
        public void onLocationChanged(Location location) {
            locationManager.removeUpdates(this);
            latitude = Double.toString(location.getLatitude()).substring(0,Double.toString(location.getLatitude()).indexOf(".") + 5);
            longitude = Double.toString(location.getLongitude()).substring(0,Double.toString(location.getLongitude()).indexOf(".") + 5);
            Lat = location.getLatitude();
            Long = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }


}

//6200EE