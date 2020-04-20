package com.example.myfirstandroidproject;

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

import com.squareup.picasso.Picasso;


public class SelectedPageActivity extends AppCompatActivity {

    TextView pageSelectedText;
    TextView pageSelectedName;
    ImageView picture;

    Rue result;

    boolean isImageViewNormal=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_page);

        pageSelectedText = findViewById(R.id.selectedPageText);
        pageSelectedName = findViewById(R.id.selectedPageName);
        picture = findViewById(R.id.imageView);

        Intent intent = getIntent();

        if(intent.getExtras() != null){
            result = (Rue) intent.getSerializableExtra("data");
        }

        pageSelectedText.setText(result.getDescription());
        pageSelectedName.setText(result.getTitre());
        pageSelectedText.setMovementMethod(new ScrollingMovementMethod());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pageSelectedText.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        loadImageFromUrl();
    }

    private void loadImageFromUrl() {
        if(result.getImage()!=null) {
            Picasso.get().load(result.getImage()).into(picture);
        }else if(result.getThumbnail()!=null){
            Picasso.get().load(result.getThumbnail()).into(picture);
        }else{
            picture.setImageResource(R.drawable.ic_visibility_off_black_24dp);
        }
    }

    public void browser1(View view) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fr.wikipedia.org/?curid="+ result.getPageId()));
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
}
