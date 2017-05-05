package com.naijab.pokemonear.network;

import android.support.annotation.RequiresApi;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

  public static final String BASE_URL = "http://sleepingforless.com:2222/";
  private static Retrofit retrofit = null;

  @RequiresApi(9)
  public static Retrofit getRetrofit() {

    if (retrofit == null) {

      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      OkHttpClient client = new OkHttpClient.Builder()
          .connectTimeout(5, TimeUnit.MINUTES)
          .readTimeout(5, TimeUnit.MINUTES)
          .addInterceptor(interceptor)
          .build();

      retrofit = new Retrofit.Builder()
          .baseUrl(BASE_URL)
          .client(client)
          .addConverterFactory(GsonConverterFactory.create())
          .build();
    }
    return retrofit;
  }
}
