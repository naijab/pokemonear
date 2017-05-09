package com.naijab.pokemonear.network;

import com.naijab.pokemonear.login.user.UserLoginModel;
import com.naijab.pokemonear.maps.pokemon.PokemonCatchableModel;
import com.naijab.pokemonear.server.ServerStatusModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PokemonServerService {

  @GET("pokemon")
  Call<ServerStatusModel> getServerStatus();

  @FormUrlEncoded
  @POST("pokemon/login")
  Call<UserLoginModel> getUserLogin(@Field("email") String email,
      @Field("password") String password);

  @GET("pokemon/catchable")
  Call<List<PokemonCatchableModel>> getPokemon(@Header("pokemon_token") String token,
      @Query("latitude") String latitude,
      @Query("longitude") String longitude);
}
