package com.naijab.pokemonear.login.user;


import android.content.Context;
import android.content.SharedPreferences;
import com.naijab.pokemonear.network.PokemonServerConnect;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginManager {

  private static UserLoginManager userLoginManager;

  private static final String KEY_PREFS = "prefs_user";
  private static final String KEY_TOKEN = "token";
  private static final String KEY_EMAIL = "email";

  private SharedPreferences mPrefs;
  private SharedPreferences.Editor mEditor;

  public static UserLoginManager getInstance() {
    if (userLoginManager == null) {
      userLoginManager = new UserLoginManager();
    }
    return userLoginManager;
  }

  public boolean saveUser(String token, String email, Context context) {
    mPrefs = context.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
    mEditor = mPrefs.edit();
    mEditor.putString(KEY_TOKEN, token);
    mEditor.putString(KEY_EMAIL, email);
    return mEditor.commit();
  }

  public void checkUserLogin(
      final Context context,
      final String username,
      final String password,
      final UserLoginManagerCallBack callBack) {

    PokemonServerConnect.getInstance().getConnection().getUserLogin(username, password).enqueue(
        new Callback<UserLoginModel>() {
          @Override
          public void onResponse(Call<UserLoginModel> call, Response<UserLoginModel> response) {

            if (response.isSuccessful()) {
              if (response.body().getMessage().equals("Login Successful")) {
                hideKeyboard();
                String token = response.body().getToken();
                boolean isSaveUser = saveUser(token, username, context);

                if (isSaveUser) {
                  callBack.onUserLoginSuccess();
                } else {
                  callBack.onUserLoginUnableSave();
                }
              } else {
                  callBack.onUserLoginInvalid();
              }
            } else {
              callBack.onUserLoginFail();
            }

          }

          @Override
          public void onFailure(Call<UserLoginModel> call, Throwable t) {
            callBack.onUserLoginError(t.getMessage());
          }
        });

  }

  private void hideKeyboard() {
  }

  public interface UserLoginManagerCallBack {

    void onUserLoginSuccess();

    void onUserLoginUnableSave();

    void onUserLoginInvalid();

    void onUserLoginFail();

    void onUserLoginError(String errorMassage);
  }


}
