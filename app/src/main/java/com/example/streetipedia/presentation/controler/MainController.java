package com.example.streetipedia.presentation.controler;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.streetipedia.Singletons;
import com.example.streetipedia.presentation.model.RestWikipediaResponseInfo;
import com.example.streetipedia.presentation.model.RestWikipediaResponseSearch;
import com.example.streetipedia.presentation.model.ResultsWikiInfo;
import com.example.streetipedia.presentation.model.ResultsWikiSearch;
import com.example.streetipedia.presentation.model.Rue;
import com.example.streetipedia.presentation.view.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

//Class that control all the logical of the program
public class MainController {

    //Variables that manage and get the position
    private LocationManager locationManager;
    private Double Lat, Long;
    private boolean getGPSlocation;

    //Variables of class that receive API response
    private ResultsWikiSearch results;
    private ResultsWikiInfo resultsInfo;

    //Variables that manage the url format
    private String url = null;
    private String url2 = null;

    //It's the list use in showList to create the recycleView
    private List<Rue> infoRues = new ArrayList<>();

    //This two list are use for sort the list of street by their distance of the user
    private Map<Integer, String> hashNomRue = new HashMap<>();
    private TreeMap<Integer, String> listNomRue = new TreeMap<>(hashNomRue);

    //List of all type of street for delete this in the name street and have just the interesting part (just for the French language)
    private List<String> listTypeVoie = Arrays.asList("Allée ", "Avenue ", "Av. ", "Boulevard ", "Carrefour ", "Chemin ", "Chaussée ",
            "Cité ", "Corniche ", "Cours ", "Domaine ", "Descente ", "Ecart ", "Esplanade ", "Faubourg ", "Grande Rue ", "Hameau ",
            "Halle ", "Impasse ", "Lieu-dit ", "Lottissement ", "Marché ", "Montée ", "Passage ", "Passerelle ", "Place ", "Plaine ",
            "Plateau ", "Promenade ", "Parvis ", "Quartier ", "Quai ", "Résidence ", "Ruelle ", "Rocade ", "Rond-Point ", "Route ",
            "Rue ", "Sentier ", "Square ", "Terre-Plein ", "Terrasse ", "Traverse ", "Villa ", "Village ");

    //Use for the animation of the reglage
    private ConstraintLayout layout;

    //Variable for knowing the state of the reglage layout
    private boolean reglage = false;

    //Variables for change simply the number of street scan
    private int pas=2;
    private int oldPas = pas;
    private int surface=1;

    //Variable for save the old state of the seekBar
    private int oldSeekBarState;

    //SharedPreferences use for save and get data of cache
    private SharedPreferences sharedPreferences;

    //Use for create a link with the MainActivity view
    private MainActivity view;

    //Variable use for deserialize list
    private Gson gson;

    //Constructor
    public MainController(MainActivity mainActivity, Gson gson,SharedPreferences sharedPreferences) {
        this.view = mainActivity;
        this.gson = gson;
        this.sharedPreferences = sharedPreferences;
    }

    //Method call in the OnCreate method of the MainActivity
    public void onStart(){

        layout = view.layout;

        //Initialise the reglage button and set a listener
        reglageButtonInitialiser(view.reglageButton);

        oldSeekBarState =1;

        //Initialise the seekBar and set a listener
        seekBarInitialiser(view.seekBar);

        //Initialise thing for call gps
        getGPSlocation = false;
        locationManager = (LocationManager) view.getSystemService(Context.LOCATION_SERVICE);

        //A way to resolve a little bug
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Manage if there is an internet connexion (if yes call API, else get data from cache and if there isn't data show a Toast)
        launch((ConnectivityManager) Objects.requireNonNull(view.getSystemService(Context.CONNECTIVITY_SERVICE)));

        //Initialise the swipe and set a listener for refresh the list when the user swipe to the top when he is in the top
        swipeContainerInitialiser(view.swipeContainer);
    }

    //Initialise the reglage button and set a listener
    private void reglageButtonInitialiser(ImageButton reglageButton){
        reglageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(layout);

                if(reglage){
                    view.constraintSetNormal.applyTo(layout);
                    reglage=false;
                }else{
                    view.constraintSetReglage.applyTo(layout);
                    view.seekBar.setEnabled(true);
                    reglage=true;
                }
            }
        });
    }

    //Initialise the seekBar and set a listener
    private void seekBarInitialiser(SeekBar seekBar){
        seekBar.setProgress(0);
        seekBar.incrementProgressBy(1);
        seekBar.setMax(4);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch(progress){
                    case 0:
                        view.surfaceTV.setText("Surface : Position");
                        view.pasTV.setText("Pas : 0m");
                        surface = 0;
                        pas = 2;
                        break;
                    case 1:
                        view.surfaceTV.setText("Surface : 100x100");
                        view.pasTV.setText("Pas : 100m");
                        surface = 1;
                        pas = 2;
                        break;
                    case 2:
                        view.surfaceTV.setText("Surface : 200x200");
                        view.pasTV.setText("Pas : 100m");
                        surface = 2;
                        pas = 2;
                        break;
                    case 3:
                        view.surfaceTV.setText("Surface : 150x150");
                        view.pasTV.setText("Pas : 50m");
                        surface = 3;
                        pas = 1;
                        break;
                    case 4:
                        view.surfaceTV.setText("Surface : 250x250");
                        view.pasTV.setText("Pas : 50m");
                        surface = 5;
                        pas = 1;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                oldPas = pas;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(oldSeekBarState <seekBar.getProgress()){
                    if(infoRues.size()!=20 || oldPas>pas){
                        refresh();
                    }
                    oldSeekBarState =seekBar.getProgress();
                }

            }
        });
    }

    //Manage if there is an internet connexion (if yes call API, else get data from cache and if there isn't data show a Toast)
    private void launch(ConnectivityManager connectivityManager){
        assert connectivityManager != null;
        if(Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED){
            //There is a connexion

            new Chargement().execute();

        }else{
            //There isn't a connexion

            //Get the list from the cache when there aren't connexion (not accurate location indeed)
            infoRues = getDataFromCache();

            if(infoRues != null){
                view.showList(infoRues);
            }else{
                Toast.makeText(view, "Veuillez vous connecter pour la première utilisation", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //Initialise the swipe and set a listener for refresh the list when the user swipe to the top when he is in the top
    private void swipeContainerInitialiser(SwipeRefreshLayout swipeContainer){
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Refresh the list with new data
                refresh();
                oldSeekBarState =view.seekBar.getProgress();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    //Refresh the list with new data
    private void refresh() {

        getGPSlocation = false;

        view.swipeContainer.setRefreshing(false);

        launch((ConnectivityManager) Objects.requireNonNull(view.getSystemService(Context.CONNECTIVITY_SERVICE)));

    }

    //Save the list in the cache when it is loaded
    private void saveList(List<Rue> rueList) {

        String jsonListRue = gson.toJson(rueList);

        sharedPreferences
                .edit()
                .putString("saveListRue", jsonListRue)
                .apply();

    }

    //Get the list from the cache when there aren't connexion (not accurate location indeed)
    private List<Rue> getDataFromCache() {
        String jsonListRue = sharedPreferences.getString("saveListRue", null);

        if (jsonListRue == null) {
            return null;
        }else{
            Type listType = new TypeToken<List<Rue>>(){}.getType();
            return (new Gson()).fromJson(jsonListRue, listType);
        }
    }

    //Make the API call to do a search about a title in wikipedia
    private void makeAPICallSearch(String search){
        Call<RestWikipediaResponseSearch> call = Singletons.getWikipediaApiSearch().getWikipediaResponseSearch("query", "1", "classic","snippet", "search", search, "", "json");
        try{
            results = Objects.requireNonNull(call.execute().body()).getQuery();
        }catch(IOException e){
            Toast.makeText(view, "error", Toast.LENGTH_SHORT).show();
        }
    }

    //Make the API call to get information about a wikipedia page
    private void makeAPICallInfo(String search){

        Call<RestWikipediaResponseInfo> call = Singletons.getWikipediaApiInfo().getWikipediaResponseInfo("query", "extracts", "1", search, "1", "2", "json");
        try{
            resultsInfo = Objects.requireNonNull(call.execute().body()).getQuery();
        }catch(IOException e){
            Toast.makeText(view, "error", Toast.LENGTH_SHORT).show();
        }
    }

    //Make the API call to get the image of a wikipedia page
    private void makeAPICallImage(String search){

        Call<String> call = Singletons.getWikipediaApiImage().getWikipediaResponseImage("query", search, "json", "pageimages");
        try{
            url = call.execute().body();
            //Format a String with an url to 2 url (one for the thumbnail and the other for the real picture)
            createUrl();

        }catch(IOException e){
            Toast.makeText(view, "error", Toast.LENGTH_SHORT).show();
        }
    }

    //Format a String with an url to 2 url (one for the thumbnail and the other for the real picture)
    private void createUrl(){
        assert url != null;
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
    }

    //Make the API call to collect the adresse and other street things with a gps location
    private void makeBingAPICall(String latitudeVar, String longitudeVar, Integer weight){

        Call<String> call = Singletons.getBingMapsApi().getBingMapsResponse("http://dev.virtualearth.net/REST/v1/Locations/" + latitudeVar + "," +  longitudeVar + "?o=json&incl=ciso2&key=AsKDhGrY05ocf_6ajFmtLjPfnPI1MxXFALXyVw9kRNrsDlSmEygCllcwizQbnUuS");
        try{
            //Format a string the collect just the name of the street (without the "rue" or the "allée" for example)
            createListNomRue(Objects.requireNonNull(call.execute().body()), weight);

        }catch(IOException e){
            Toast.makeText(view, "error", Toast.LENGTH_SHORT).show();
        }
    }

    //Format a string the collect just the name of the street (without the "rue" or the "allée" for example)
    private void createListNomRue(String response,Integer weight){
        assert response != null;
        if(response.contains("baseStreet")){
            response = response.substring(response.indexOf("baseStreet"));
            response = response.substring(0, response.indexOf("intersectionType")-3);

            String[] rue = response.split(",");

            while(hashNomRue.containsKey(weight)){
                weight++;
            }

            for(int i=0;i<rue.length;i++){
                rue[i] = rue[i].substring(rue[i].indexOf(":")+1);
                rue[i] = rue[i].replace("\"","");
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
            }
        }
    }

    //Have a list of things and collect all information with the Wikipedia API
    private void createListRue(List<String> nomsRue){

        infoRues.clear();

        for(int i=0; i<nomsRue.size(); i++){
            //Collect all the data for a street name
            ajoutOneRue(i, nomsRue);
        }
    }

    //Collect all the data for a street name
    private void ajoutOneRue(int i, List<String> nomsRue){
        Rue newRue = new Rue();

        newRue.setNomRue(nomsRue.get(i));

        String titre = nomsRue.get(i);
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

    //Main thing that manage all component
    private void chargement(){
        if(!getGPSlocation){
            getGPSlocation = true;

            try {
                TimeUnit.MILLISECONDS.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            collectBingApi();

            List<String> nomsRue = new ArrayList<>(listNomRue.values());

            if(nomsRue.size()>=20){
                nomsRue = nomsRue.subList(0,20);
            }

            createListRue(nomsRue);

        }
    }

    //Method that get the location of the user and, depending of the setting of the user, collect a list of street name
    private void collectBingApi(){

        listNomRue.clear();
        hashNomRue.clear();

        int distanceMax = surface;

        double latDist = 0.00045*pas;//50m * le pas
        double longDist = 0.00075*pas;//50m * le pas

        //Small method that wait that the location have been getting
        waitLocationListener();

        //Format gps coordinate to prevent a bug
        formatGPSLocation();

        Lat -= distanceMax * latDist;
        Long -= distanceMax * longDist;

        for(int i=0;i<distanceMax*2+1;i++){
            for(int j=0;j<distanceMax*2+1;j++){
                if(Double.toString(Lat+(i*latDist)).length()-Double.toString(Lat+(i*latDist)).indexOf(".")+1<5){
                    Lat += 0.00001;
                }
                if(Double.toString(Long+(j*longDist)).length()-Double.toString(Long+(j*longDist)).indexOf(".")+1<5){
                    Long += 0.00001;
                }

                String stringLat = Double.toString(Lat+(i*latDist));
                String stringLong = Double.toString(Long+(j*longDist));
                int weight = (int) (Math.sqrt(((i-distanceMax)*(i-distanceMax))+((j-distanceMax)*(j-distanceMax)))*1000);
                makeBingAPICall(stringLat, stringLong, weight);
            }
        }

        Lat=null;
        Long=null;

        listNomRue.putAll(hashNomRue);
    }

    //Small method that wait that the location have been getting
    private void waitLocationListener(){
        //A way to wait the GPS Location and not do the Bing API call without location
        while (Lat==null && Long==null){
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //Format gps coordinate to prevent a bug
    private void formatGPSLocation(){
        assert Lat != null;
        if(Lat.toString().length()-Lat.toString().indexOf(".")+1<5){
            Lat += 0.00001;
        }
        Lat = Double.parseDouble(Lat.toString().substring(0,Lat.toString().indexOf(".")+6));

        assert Long != null;
        if(Long.toString().length()-Long.toString().indexOf(".")+1<5){
            Long += 0.00001;
        }
        Long = Double.parseDouble(Long.toString().substring(0,Long.toString().indexOf(".")+6));
    }

    //Private class that permit to do Asynchronous task (use for the waiting with the progress bar)
    @SuppressLint("StaticFieldLeak")
    public class Chargement extends AsyncTask<Void, Void, Void> implements LocationListener {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            onPreExecuteViewChange();

            AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);

            view.progressBarHolder.setAnimation(inAnimation);
            view.progressBarHolder.setVisibility(View.VISIBLE);

            if (ActivityCompat.checkSelfPermission(view.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);

            view.progressBarHolder.setAnimation(outAnimation);
            view.progressBarHolder.setVisibility(View.GONE);

            //Save the list in the cache when it is loaded
            saveList(infoRues);
            view.showList(infoRues);

            onPostExecuteViewChange();
        }

        @Override
        protected Void doInBackground(Void... params) {
            chargement();
            return null;
        }

        private void onPreExecuteViewChange(){
            view.recyclerView.setEnabled(false);
            view.recyclerView.setVisibility(View.GONE);
            view.reglageButton.setEnabled(false);
            view.reglageButton.setVisibility(View.GONE);
            view.rapiditeTV.setEnabled(false);
            view.rapiditeTV.setVisibility(View.INVISIBLE);
            view.quantiteTV.setEnabled(false);
            view.quantiteTV.setVisibility(View.INVISIBLE);
            view.surfaceTV.setEnabled(false);
            view.surfaceTV.setVisibility(View.INVISIBLE);
            view.pasTV.setEnabled(false);
            view.pasTV.setVisibility(View.INVISIBLE);
            view.rectangle.setEnabled(false);
            view.rectangle.setVisibility(View.INVISIBLE);
            view.seekBar.setEnabled(false);
            view.seekBar.setVisibility(View.INVISIBLE);
            view.recyclerViewStatue=false;
        }

        private void onPostExecuteViewChange(){
            view.recyclerView.setVisibility(View.VISIBLE);
            view.recyclerView.setEnabled(true);
            view.reglageButton.setVisibility(View.VISIBLE);
            view.reglageButton.setEnabled(true);
            if(reglage) {
                view.rapiditeTV.setEnabled(true);
                view.rapiditeTV.setVisibility(View.VISIBLE);
                view.quantiteTV.setEnabled(true);
                view.quantiteTV.setVisibility(View.VISIBLE);
                view.surfaceTV.setEnabled(true);
                view.surfaceTV.setVisibility(View.VISIBLE);
                view.pasTV.setEnabled(true);
                view.pasTV.setVisibility(View.VISIBLE);
                view.rectangle.setEnabled(true);
                view.rectangle.setVisibility(View.VISIBLE);
                view.seekBar.setEnabled(true);
                view.seekBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLocationChanged(Location location) {
            locationManager.removeUpdates(this);
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
