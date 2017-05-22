package com.naijab.pokemonear.login;

import android.content.Intent;
import android.os.Bundle;
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
import com.naijab.pokemonear.login.user.UserLoginManager;
import com.naijab.pokemonear.login.user.UserLoginManager.UserLoginManagerCallBack;
import com.naijab.pokemonear.maps.MapsActivity;
import com.naijab.pokemonear.network.PokemonServerManager;
import com.naijab.pokemonear.network.PokemonServerManager.CheckServerStatusCallBack;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class LoginFragment extends Fragment {

  private Button btnLogin;
  private EditText editUsername;
  private EditText editPassword;
  private final String TAG = getClass().getSimpleName();

  public LoginFragment() {
    super();
  }

  public static LoginFragment newInstance() {
    LoginFragment fragment = new LoginFragment();
    Bundle args = new Bundle();
    // TODO "Login" ควรทำเป็น Static Final String ไว้
    // TODO แต่ดูแล้วเหมือนจะไม่ได้ใช้ จะใส่ทำไม...
    args.putBoolean("Login", false);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init(savedInstanceState);

    if (savedInstanceState != null) {
      onRestoreInstanceState(savedInstanceState);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_login, container, false);
    initInstances(rootView, savedInstanceState);
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
    btnLogin.setOnClickListener(onLoginListener);
  }

  private View.OnClickListener onLoginListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      checkServer();
    }
  };

  public void initViewFormLogin() {
    String email = editUsername.getText().toString();
    String password = editPassword.getText().toString();

    if (TextUtils.isEmpty(email)) {
      // TODO อย่า Hardcode String ที่เป็นข้อความแสดงให้ User
      // TODO เปลี่ยนไปใช้ String XML ซะ
      editUsername.setError("Please fill Email.");
      return;
    } else if (TextUtils.isEmpty(password)) {
      // TODO อย่า Hardcode String ที่เป็นข้อความแสดงให้ User
      // TODO เปลี่ยนไปใช้ String XML ซะ
      editPassword.setError("Please fill Password.");
      return;
    }

    checkLogin(email, password);
  }

  private void checkServer() {
    PokemonServerManager.getInstance().checkServerStatus(new CheckServerStatusCallBack() {
      @Override
      public void onServerActive(String ServerStatus) {
        showToast(ServerStatus);
        initViewFormLogin();
      }

      @Override
      public void onServerDown(String ServerStatus) {
        showToast(ServerStatus);
      }

      @Override
      public void onServerError(String ServerError) {
        showToast(ServerError);
      }
    });
  }

  private void checkLogin(String username, String password) {
    UserLoginManager.getInstance().checkUserLogin(getActivity(), username, password,
        new UserLoginManagerCallBack() {
          @Override
          public void onUserLoginSuccess() {
            // TODO ใช้ String Resource แทน Hardcode
            showToast("Login Success");
            hideKeyboard();
            goMapsActivity();
          }

          @Override
          public void onUserLoginUnableSave() {
            // TODO ใช้ String Resource แทน Hardcode
            showToast("Can't save user.");
          }

          @Override
          public void onUserLoginInvalid() {
            // TODO ใช้ String Resource แทน Hardcode
            showToast("Email or Password invalid.");
          }

          @Override
          public void onUserLoginFail() {
            // TODO ใช้ String Resource แทน Hardcode
            showToast("Login has error.");
          }

          @Override
          public void onUserLoginError(String errorMassage) {
            showToast(errorMassage);
          }
        });
  }

  private void goMapsActivity() {
    Intent i = new Intent(getActivity(), MapsActivity.class);
    startActivity(i);
  }

  private void showToast(String text) {
    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
  }

  private void hideKeyboard() {
    try {
      InputMethodManager imm = (InputMethodManager) getActivity()
          .getSystemService(INPUT_METHOD_SERVICE);
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
  }
}
