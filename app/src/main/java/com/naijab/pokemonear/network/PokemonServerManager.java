package com.naijab.pokemonear.network;

import com.naijab.pokemonear.server.ServerStatusModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonServerManager {

  private static PokemonServerManager pokemonServerManager;

  public static PokemonServerManager getInstance() {
    if (pokemonServerManager == null) {
      pokemonServerManager = new PokemonServerManager();
    }
    return pokemonServerManager;
  }

  public void checkServerStatus(final CheckServerStatusCallBack callBack) {
    PokemonServerConnect.getInstance().getConnection().getServerStatus().enqueue(
        new Callback<ServerStatusModel>() {
          @Override
          public void onResponse(Call<ServerStatusModel> call,
              Response<ServerStatusModel> response) {

            String ServerStatusMassage = response.body().getMessage();

            if (response.isSuccessful()) {

              if (ServerStatusMassage.equals("Server is active")) {

                callBack.onServerActive(ServerStatusMassage);

              } else {

                callBack.onServerDown(ServerStatusMassage);

              }
            }
          }

          @Override
          public void onFailure(Call<ServerStatusModel> call, Throwable t) {
            callBack.onServerError(t.getMessage());
          }
        });
  }

  public interface CheckServerStatusCallBack {

    void onServerActive(String ServerStatus);

    void onServerDown(String ServerStatus);

    void onServerError(String ServerError);
  }


}
