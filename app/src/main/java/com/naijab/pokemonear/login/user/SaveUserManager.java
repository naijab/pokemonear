package com.naijab.pokemonear.login.user;

import android.content.Context;
import android.content.SharedPreferences;
import com.naijab.pokemonear.PokemoNearApplication;

public class SaveUserManager {

  private static final String KEY_PREFS = "prefs_user";
  private static final String KEY_TOKEN = "token";
  private static final String KEY_EMAIL = "email";

  private SharedPreferences mPrefs;
  private SharedPreferences.Editor mEditor;

  private static SaveUserManager saveUserManager;

  public static SaveUserManager getInstance() {
    if (saveUserManager == null) {
      saveUserManager = new SaveUserManager();
    }
    return saveUserManager;
  }

  public boolean saveUser(String token, String email) {
    mPrefs = PokemoNearApplication.getContext().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
    mEditor = mPrefs.edit();
    mEditor.putString(KEY_TOKEN, token);
    mEditor.putString(KEY_EMAIL, email);
    return mEditor.commit();
  }



}
