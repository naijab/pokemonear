package com.naijab.pokemonear.maps.pokemon;

import com.naijab.pokemonear.network.PokemonServerConnect;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonManager {

  private static PokemonManager pokemonManager;

  public static PokemonManager getInstance() {
    if (pokemonManager == null) {
      pokemonManager = new PokemonManager();
    }
    return pokemonManager;
  }

  public void findPokemon(String token,
                          String latitude,
                          String longitude,
                          final FindPokemonCallBack callBack){

    PokemonServerConnect.getInstance().getConnection().getPokemon(token,
        latitude,
        longitude).enqueue(new Callback<List<PokemonCatchableModel>>() {
      @Override
      public void onResponse(Call<List<PokemonCatchableModel>> call,
          Response<List<PokemonCatchableModel>> response) {
        if (response.isSuccessful()) {
          callBack.onDetectPokemon();
        }else {
          callBack.onDetectPokemonFail();
        }
      }

      @Override
      public void onFailure(Call<List<PokemonCatchableModel>> call, Throwable t) {
        callBack.onServerError(t.getMessage());
      }
    });


  }

  public interface FindPokemonCallBack{

    void onDetectPokemon();

    void onDetectPokemonFail();

    void onServerError(String errorMassage);
  }


}
