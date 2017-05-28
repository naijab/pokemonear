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

    public void findRandomPokemon(String token,
                                  String latitude,
                                  String longitude,
                                  final FindPokemonCallBack callBack) {

        PokemonServerConnect.getInstance().getConnection().getPokemon(token,
                latitude,
                longitude).enqueue(new Callback<PokemonCatchableModel>() {
            @Override
            public void onResponse(Call<PokemonCatchableModel> call,
                                   Response<PokemonCatchableModel> response) {

                if (response.isSuccessful()) {
                    PokemonCatchableModel pokemonCatchableModel = response.body();
                    int pokemonDetectSize = pokemonCatchableModel.getData().size();

                    for (int i = 0; i < pokemonDetectSize; i++) {
//                        pokemonDataList = new ArrayList<>(Arrays.asList(pokemonCatchableModel.getData()));
//                        PokemonDataModel pokemonDataModel = pokemonCatchableModel.getData().get(i);
//                        String pokemonID = pokemonDataModel.getId();
//                        String pokemonName = pokemonDataModel.getName();
//                        String pokemonNumber = pokemonDataModel.getNumber();
//                        double pokemonLatitude = pokemonDataModel.getLatitude();
//                        double pokemonLongitude = pokemonDataModel.getLongitude();
//                        long pokemonLife = pokemonDataModel.getExpirationTimestamp();

                        List<PokemonDataModel> list = pokemonCatchableModel.getData();
                        String pokemonID = list.get(i).getId();
                        String pokemonName = list.get(i).getName();
                        String pokemonNumber = list.get(i).getNumber();
                        double pokemonLatitude = list.get(i).getLatitude();
                        double pokemonLongitude = list.get(i).getLongitude();
                        long pokemonLife = list.get(i).getExpirationTimestamp();

                        PokemonDataModel pokemonDataModel = new PokemonDataModel(
                            pokemonID, pokemonName,
                            pokemonNumber, pokemonLatitude,
                            pokemonLongitude, pokemonLife);

                        callBack.onDetectPokemon(pokemonDataModel);
                    }
                } else {
                    String message = response.body().getMessage();
                    callBack.onDetectPokemonFail(message);
                }


            }

            @Override
            public void onFailure(Call<PokemonCatchableModel> call, Throwable t) {
                callBack.onServerError(t.getMessage());
            }
        });

    }

    public interface FindPokemonCallBack {

        void onDetectPokemon(PokemonDataModel pokemonDataModel);

        void onDetectPokemonFail(String message);

        void onServerError(String errorMassage);
    }


}
