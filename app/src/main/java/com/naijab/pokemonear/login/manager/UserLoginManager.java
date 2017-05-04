package com.naijab.pokemonear.login.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.naijab.pokemonear.network.RetrofitService;
import com.naijab.pokemonear.network.ServiceInterface;
import com.naijab.pokemonear.server.ServerStatusModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginManager {

    private final String KEY_PREFS = "prefs_user";
    private final String KEY_TOKEN = "token";
    private final String KEY_EMAIL = "email";

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    private boolean serverStatus = false;
    Context context;

    public UserLoginManager(Context context){
        mPrefs = context.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();
    }

    public boolean saveUser(String token, String email) {
        mEditor.putString(KEY_TOKEN, token);
        mEditor.putString(KEY_EMAIL, email);
        return mEditor.commit();
    }
}
