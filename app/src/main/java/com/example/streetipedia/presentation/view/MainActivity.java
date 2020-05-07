package com.example.streetipedia.presentation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.streetipedia.R;
import com.example.streetipedia.Singletons;
import com.example.streetipedia.presentation.controler.MainController;
import com.example.streetipedia.presentation.model.Rue;

import java.util.List;

//Main class that create and manage the first activity
public class MainActivity extends AppCompatActivity implements ListAdapter.SelectedPage{

    public boolean recyclerViewStatue; //Use to control the access of the the new activity (to not allow the access of the detail during refresh for example)

    public SwipeRefreshLayout swipeContainer;

    public RecyclerView recyclerView;

    public FrameLayout progressBarHolder;

    public ImageButton reglageButton;

    //3 Constraint for create beautiful animation when the user access to the reglage function
    public ConstraintLayout layout;
    public ConstraintSet constraintSetNormal = new ConstraintSet();
    public ConstraintSet constraintSetReglage = new ConstraintSet();

    public SeekBar seekBar;
    public TextView rapiditeTV;
    public TextView quantiteTV;
    public TextView surfaceTV;
    public TextView pasTV;
    public View rectangle;

    //Main method that create the view
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        declarationLayout(); //Declaration of some object of the main activity layout

        //Creation of a controller
        MainController mainController = new MainController(
                this,
                Singletons.getGson(),
                Singletons.getSharedPreferences(getApplicationContext())
        );
        mainController.onStart();//Call the controller

        recyclerViewStatue = false;//Desactivation of the access of the detail view

    }

    //Declaration of some object of the main activity layout
    public void declarationLayout(){
        layout = findViewById(R.id.layout);

        constraintSetNormal.clone(layout);
        constraintSetReglage.clone(this, R.layout.activity_main_reglage);

        progressBarHolder = findViewById(R.id.progressBarHolder);
        recyclerView = findViewById(R.id.recycler_view);

        reglageButton = findViewById(R.id.imageButton);

        swipeContainer = findViewById(R.id.swipeContainer);

        seekBar = findViewById(R.id.seekBar);

        rapiditeTV = findViewById(R.id.rapidite);
        quantiteTV = findViewById(R.id.quantite);
        surfaceTV = findViewById(R.id.surface);
        pasTV = findViewById(R.id.pas);
        rectangle = findViewById(R.id.myRectangleView);
    }

    //Method that show the list of street in the recyclerView
    public void showList(List<Rue> rueList) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ListAdapter mAdapter = new ListAdapter(rueList, this);
        recyclerView.setAdapter(mAdapter);

        recyclerViewStatue = true;
    }

    //Call of the second view
    @Override
    public void selectedPage(Rue result) {
        if(recyclerViewStatue){
            startActivity(new Intent(MainActivity.this, SelectedPageActivity.class).putExtra("data", result));
        }
    }

}