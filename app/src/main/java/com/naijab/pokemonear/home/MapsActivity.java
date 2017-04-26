package com.naijab.pokemonear.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.naijab.pokemonear.R;

public class MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, MapsFragment.newInstance())
                    .commit();
        }
    }
}
