package com.naijab.pokemonear.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.naijab.pokemonear.R;
import com.naijab.pokemonear.login.manager.UserLoginManager;
import com.naijab.pokemonear.maps.MapsActivity;
import com.naijab.pokemonear.network.RetrofitService;
import com.naijab.pokemonear.network.ServiceInterface;
import com.naijab.pokemonear.server.ServerStatusModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button btnLogin;
    private EditText editUsername;
    private EditText editPassword;
    private Boolean serverStatus = false;
    private Boolean loginStatus = false;
    private String serverStatusMessage = "";
    private final String TAG = getClass().getSimpleName();
    private UserLoginManager mManager;

    public LoginFragment() {
        super();
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putBoolean("Login", false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initInstances(rootView, savedInstanceState);
        checkServer();
        mManager = new UserLoginManager(getActivity());
        return rootView;
    }


    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        btnLogin = (Button) rootView.findViewById(R.id.btn_login);
        editUsername = (EditText) rootView.findViewById(R.id.edit_email);
        editPassword = (EditText) rootView.findViewById(R.id.edit_password);
        btnLogin.setOnClickListener(this);
        serverStatus = false;
        loginStatus = false;


        int id = getResources().getIdentifier("ic_pokemon_no_" + 2,
                "drawable", getActivity().getPackageName());
        Log.i("Drawable",  ""+ id);
    }


    private void checkServer() {
        ServiceInterface apiService = RetrofitService.getRetrofit().create(ServiceInterface.class);
        Call<ServerStatusModel> checkServer = apiService.getServerStatus();
        checkServer.enqueue(new Callback<ServerStatusModel>() {
            @Override
            public void onResponse(Call<ServerStatusModel> call, Response<ServerStatusModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equals("Server is active")) {
                        serverStatusMessage = "Server is active";
                        showToast(serverStatusMessage);
                        serverStatus = true;
                    } else {
                        serverStatusMessage = "Server is deactivate";
                        showToast(serverStatusMessage);
                        serverStatus = false;
                    }
                } else {
                    showToast("Server has error.");
                }
            }

            @Override
            public void onFailure(Call<ServerStatusModel> call, Throwable t) {
                showToast("Sorry service has error: " + t);
            }
        });
    }

    public void initViewForm() {
        String email = editUsername.getText().toString();
        String password = editPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            editUsername.setError("Please fill Email.");
            return;
        } else if (TextUtils.isEmpty(password)) {
            editPassword.setError("Please fill Password.");
            return;
        }

        checkLogin(email, password);
    }

    private void checkLogin(String email, String password) {

        ServiceInterface apiService = RetrofitService.getRetrofit().create(ServiceInterface.class);
        Call<UserLoginModel> checkServer = apiService.getUserLogin(email, password);
        checkServer.enqueue(new Callback<UserLoginModel>() {
            @Override
            public void onResponse(Call<UserLoginModel> call, Response<UserLoginModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equals("Login Successful")) {
                        hideKeyboard();
                        String token = response.body().getToken();
                        String email = editUsername.getText().toString();

                        boolean isSaveUser = mManager.saveUser(token, email);
                        if (isSaveUser){
                            showToast("Login Successful:" + token);
                            loginStatus = true;
                            goMapsActivity();
                        }else {
                            showToast("Can't Save Successful:" + token);
                        }
                    } else {
                        showToast(getResources().getString(R.string.login_error));
                        loginStatus = false;
                    }
                } else {
                    showToast("Server has error.");
                    Log.i("Login fail", "");
                }
            }

            @Override
            public void onFailure(Call<UserLoginModel> call, Throwable t) {
                showToast("Sorry service has error: " + t);
            }
        });
    }

    private void goMapsActivity() {
        Intent i = new Intent(getActivity(), MapsActivity.class);
        startActivity(i);
    }

    private Boolean checkForm() {

        if (editUsername != null && editPassword != null)
            return true;

        return false;
    }

    private void tryToLogin() {
        if (!loginStatus)
            initViewForm();
    }

    private void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            Log.e(TAG, "hideKeyboard: " + e);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loginStatus = false;
    }

    @Override
    public void onClick(View v) {

        if (v == btnLogin) {
            if (checkForm()) {
                if (serverStatus) {
                    tryToLogin();
                } else {
                    showToast(serverStatusMessage);
                }

            } else {
                showToast("Please fill email and password.");
            }

        }

    }


}
