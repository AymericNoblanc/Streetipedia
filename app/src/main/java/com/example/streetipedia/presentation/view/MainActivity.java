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

public class MainActivity extends AppCompatActivity implements ListAdapter.SelectedPage{

    public boolean recyclerViewStatue;

    public SwipeRefreshLayout swipeContainer;

    public RecyclerView recyclerView;

    public FrameLayout progressBarHolder;

    public ImageButton reglageButton;

    public ConstraintLayout layout;
    public ConstraintSet constraintSetNormal = new ConstraintSet();
    public ConstraintSet constraintSetReglage = new ConstraintSet();

    public SeekBar seekBar;
    public TextView rapiditeTV;
    public TextView quantiteTV;
    public TextView surfaceTV;
    public TextView pasTV;
    public View rectangle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        declarationLayout();

        MainController mainController = new MainController(
                this,
                Singletons.getGson(),
                Singletons.getSharedPreferences(getApplicationContext())
        );
        mainController.onStart();

        recyclerViewStatue = false;

    }

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

    public void showList(List<Rue> rueList) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ListAdapter mAdapter = new ListAdapter(rueList, this);
        recyclerView.setAdapter(mAdapter);

        recyclerViewStatue = true;
    }

    @Override
    public void selectedPage(Rue result) {
        if(recyclerViewStatue){
            startActivity(new Intent(MainActivity.this, SelectedPageActivity.class).putExtra("data", result));
        }
    }

}