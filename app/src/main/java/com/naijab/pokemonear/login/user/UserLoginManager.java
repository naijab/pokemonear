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

    // TODO Method นี้ไม่ใช่คำสั่งที่ควรจะอยู่ใน Network Manager
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
                            // TODO อย่า Hardcode String แบบนี้ และจริงๆแล้วไม่จำเป็นต้องเช็คจาก Message ด้วยซ้ำ
                            // TODO มันเป็นแค่คำที่ใช้แสดงให้ User เห็น ถ้าวันไหนทำหลายภาษาขึ้นมา ก็ต้อง equals หลายๆข้อความหรือ?
                            if (response.body().getMessage().equals("Login Successful")) {
                                // TODO hideKeyboard() อันนี้คำสั่งของ UI ไม่ควรใส่ไว้ใน Network Manager
                                // TODO ถ้าจะใส่ก็ไปใส่ไว้ที่ Activity/Fragment นู่น
                                hideKeyboard();
                                String token = response.body().getToken();
                                // TODO อันนี้ก็เป็นส่วนที่เชื่อมต่อกับ SharedPreference ที่ไม่ควรอยู่ใน Network Manager เหมือนกัน
                                // TODO กลายเป็นว่าต้องโยน Context เข้ามาในนี้เพื่อคำสั่งนี้ ทั้งๆที่คลาสนี้ไม่จำเป็นต้องใช้ด้วยซ้ำ
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
