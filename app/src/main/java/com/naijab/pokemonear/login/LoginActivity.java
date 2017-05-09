package com.naijab.pokemonear.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.naijab.pokemonear.R;

public class LoginActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.frame_container, LoginFragment.newInstance())
          .commit();
    }
  }
}
