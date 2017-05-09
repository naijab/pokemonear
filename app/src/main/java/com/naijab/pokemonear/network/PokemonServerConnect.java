package com.naijab.pokemonear.network;

import android.support.annotation.RequiresApi;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonServerConnect {

  private static PokemonServerConnect pokemonServerConnect;
  private static final String BASE_URL = "http://sleepingforless.com:2222/";

  public static PokemonServerConnect getInstance() {
    if (pokemonServerConnect == null) {
      pokemonServerConnect = new PokemonServerConnect();
    }
    return pokemonServerConnect;
  }

  private PokemonServerService pokemonServerService;

  @RequiresApi(9)
  public PokemonServerService getConnection(){
    if (pokemonServerService == null){
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      OkHttpClient client = new OkHttpClient.Builder()
          .connectTimeout(5, TimeUnit.MINUTES)
          .readTimeout(5, TimeUnit.MINUTES)
          .addInterceptor(interceptor)
          .build();

      Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(BASE_URL)
          .client(client)
          .addConverterFactory(GsonConverterFactory.create())
          .build();
      pokemonServerService = retrofit.create(PokemonServerService.class);
    }
    return pokemonServerService;
  }

}
