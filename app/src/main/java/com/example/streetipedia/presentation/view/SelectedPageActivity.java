package com.example.streetipedia.presentation.view;

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

import com.example.streetipedia.R;
import com.example.streetipedia.presentation.model.Rue;
import com.squareup.picasso.Picasso;

//Class that create and manage the second activity
public class SelectedPageActivity extends AppCompatActivity {

    TextView pageSelectedText;
    TextView pageSelectedName;
    ImageView picture;

    Rue result;

    //Main method that create the view
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

        assert result != null;
        pageSelectedText.setText(result.getDescription());
        pageSelectedName.setText(result.getTitre());
        pageSelectedText.setMovementMethod(new ScrollingMovementMethod());

        //Set the justification more beautiful but it's just available after a certain sdk
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pageSelectedText.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        loadImageFromUrl(); //Method that load a image from an url
    }

    //Method that load a image from an url
    private void loadImageFromUrl() {
        if(result.getImage()!=null) {
            Picasso.get().load(result.getImage()).into(picture);//Show the image
        }else if(result.getThumbnail()!=null){
            Picasso.get().load(result.getThumbnail()).into(picture);//Show the thumbnail
        }else{
            picture.setImageResource(R.drawable.ic_visibility_off_black_24dp);//Show a no view picture
        }
    }

    //Method that manage the button "Ouvrir la page Wikipédia"
    public void browser1(View view) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fr.wikipedia.org/?curid="+ result.getPageId()));
        startActivity(browserIntent);
    }
}
