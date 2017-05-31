package com.naijab.pokemonear.login.user;


import com.naijab.pokemonear.network.PokemonServerConnect;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginManager {

  private static UserLoginManager userLoginManager;

  public static UserLoginManager getInstance() {
    if (userLoginManager == null) {
      userLoginManager = new UserLoginManager();
    }
    return userLoginManager;
  }

  public void checkUserLogin(
      final String username,
      final String password,
      final UserLoginManagerCallBack callBack) {

    PokemonServerConnect.getInstance().getConnection().getUserLogin(username, password).enqueue(
        new Callback<UserLoginModel>() {
          @Override
          public void onResponse(Call<UserLoginModel> call, Response<UserLoginModel> response) {

            if (response.isSuccessful()) {
              if (response.body().getMessage() != null) {
                String token = response.body().getToken();
                boolean isSaveUser = SaveUserManager.getInstance().saveUser(token, username);
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

  public interface UserLoginManagerCallBack {

    void onUserLoginSuccess();

    void onUserLoginUnableSave();

    void onUserLoginInvalid();

    void onUserLoginFail();

    void onUserLoginError(String errorMassage);
  }


}
