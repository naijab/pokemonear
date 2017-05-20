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
                            // TODO อย่า Hardcode String แบบนี้ และจริงๆแล้วไม่จำเป็นต้องเช็คจาก Message ด้วยซ้ำ
                            // TODO มันเป็นแค่คำที่ใช้แสดงให้ User เห็น ถ้าวันไหนทำหลายภาษาขึ้นมา ก็ต้อง equals หลายๆข้อความหรือ?
                            if (ServerStatusMassage.equals("Server is active")) {
                                callBack.onServerActive(ServerStatusMassage);
                            } else {
                                // TODO ถ้า Server พี่ Down จริงๆ พี่จะส่งมาบอกเราได้ยังไง ฮ่าๆ
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
