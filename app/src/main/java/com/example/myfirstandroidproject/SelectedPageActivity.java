package com.example.myfirstandroidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SelectedPageActivity extends AppCompatActivity {

    TextView pageSelectedText;
    TextView pageSelectedName;
    ImageView picture;

    ResultsWikiInfo resultsInfo;

    String search2;

    String url = null;

    boolean isImageViewNormal=true;

    private static final String BASE_URL = "https://en.wikipedia.org/w/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_page);

        pageSelectedText = findViewById(R.id.selectedPageText);
        pageSelectedName = findViewById(R.id.selectedPageName);

        Intent intent = getIntent();

        picture = findViewById(R.id.imageView);

        if(intent.getExtras() != null){
            ResultWikiSearch result = (ResultWikiSearch) intent.getSerializableExtra("data");

            if (result != null) {
                search2 = Integer.toString(result.getPageid());
            }

            makeAPICallImage(search2);

            makeAPICallInfo(search2);
        }

    }

    private void loadImageFromUrl(String url) {
        Picasso.get().load(url).into(picture);
    }

    public void makeAPICallInfo(String search){

        Call<RestWikipediaResponseInfo> call = callRestApiWikipedia2(search);
        call.enqueue(new Callback<RestWikipediaResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<RestWikipediaResponseInfo> call, @NonNull Response<RestWikipediaResponseInfo> response) {
                if(response.isSuccessful() && response.body() != null){
                    resultsInfo = response.body().getQuery();
                    pageSelectedText.setText(resultsInfo.getPages().get(0).getExtract());
                    pageSelectedName.setText(resultsInfo.getPages().get(0).getTitle());
                    pageSelectedText.setMovementMethod(new ScrollingMovementMethod());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        pageSelectedText.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                    }

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

    private void showError() {

        Toast.makeText(this, "API Error", Toast.LENGTH_SHORT).show();
    }

    private Call<RestWikipediaResponseInfo> callRestApiWikipedia2(String search) {

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

    public void browser1(View view) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/"+ resultsInfo.getPages().get(0).getTitle()));
        startActivity(browserIntent);
    }

    //tests -> don't work
    public void animateImageView(View view) {
        if(isImageViewNormal){
            picture.getLayoutParams().width = view.getWidth();
            picture.getLayoutParams().height = view.getHeight();
            isImageViewNormal=false;
        }else{
            picture.getLayoutParams().width = 145;
            picture.getLayoutParams().height = 145;
            isImageViewNormal=true;
        }
    }


    public void makeAPICallImage(String search){

        Call<String> call = callRestApiWikipediaImage(search);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful() && response.body() != null){
                    url = response.body();
                    if(url.contains("https://upload.wikimedia.org")) {
                            url = url.substring(url.indexOf("https://upload.wikimedia.org"));
                        if(url.contains("svg")){
                            url = url.substring(0,url.indexOf(".png"));
                            url = url.concat(".png");
                        }else {
                            url = url.replace("/thumb", "");
                            url = url.substring(0, url.indexOf(".jpg"));
                            url = url.concat(".jpg");
                        }
                            loadImageFromUrl(url);
                    }else{
                        picture.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                    }
                }else{
                    showError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showError();
            }
        });
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


}
