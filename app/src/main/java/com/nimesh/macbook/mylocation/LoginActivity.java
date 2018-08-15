package com.nimesh.macbook.mylocation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import api.ApiClient;
import api.ApiInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import response.ErrorResponse;
import response.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.LogoutExpireDialog;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login)
    AppCompatButton _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            // googleMap.setMyLocationEnabled(true);
            // googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            // Toast.makeText(this, R.string.error_permission_map, Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[] {
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    114);
        }

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        boolean isLoged=pref.getBoolean("isLog", false); // getting boolean

        if(isLoged)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

       // _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed

                        doLogin();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String userName,String password,String userId) {
        _loginButton.setEnabled(true);



        editor.putBoolean("isLog", true); // Storing boolean - true/false
        editor.putString("Username",  userName); // Storing string
        editor.putString("Password", password); // Storing string  userId
        editor.putString("userId",  userId);
        editor.commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private void doLogin() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
//        LoginService apiService =
//                ServiceGenerator.createService(LoginService.class, MySharedPreferences.getHeaderUsername(), MySharedPreferences.getHeaderPassword());


      //  RequestBody name = RequestBody.create(MediaType.parse("text/plain"), _nameText.getText().toString());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), _emailText.getText().toString());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), _passwordText.getText().toString());
     //   RequestBody reEnterPassword = RequestBody.create(MediaType.parse("text/plain"), _reEnterPasswordText.getText().toString());

          Call<JsonObject> call = apiService.doLogin(email, password);

        // Call<JsonObject> call = apiService.doLogin (new LoginRequest(email.getText().toString(), password.getText().toString(), FirebaseInstanceId.getInstance().getToken(), "android", "2815b8158cbb1bcca4b2cfb33459af0d",android_id));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {

                    Log.i(TAG, "onResponse: " + response.body());
                    if (response.body().getAsJsonObject("data") != null) {

                        Gson gson = new GsonBuilder().setLenient().create();
                        LoginResponse json = gson.fromJson(response.body().getAsJsonObject("data"), LoginResponse.class);

                        Log.i(TAG, "onResponse: " + json);
                        onLoginSuccess(json.getUserProfile().getName(),json.getUserProfile().getEmail(),json.getUserId());

                    } else {
                        if (response.body().getAsJsonObject("error") != null) {
                            //   setSuccessDialog(0, response.body().getAsJsonObject("error").getAsJsonObject("data").get("message").toString().replace("\"",""));
                     //       Log.i(TAG, "onResponse: " + response.body().getAsJsonObject("error"));
                        }
                    }
                }else {
             //'[p

                    Gson gson = new Gson();
                    ErrorResponse errorResponse = gson.fromJson(
                            response.errorBody().charStream(),
                            ErrorResponse.class);

                    Toast.makeText(getApplicationContext(), "Sorry! We could not find any match" , Toast.LENGTH_SHORT).show();


                    //[text={"message":"Sorry! We could not find any match","redirect":""}]

//                    switch (response.code()) {
//                        case 400:
//                            new LogoutExpireDialog(this,errorResponse.getError().getData().getMessage());
//
//                            break;
//                        case 401:
//                            new LogoutExpireDialog(this,errorResponse.getError().getData().getMessage());
//                            break;
//                        case 403:
//                            new LogoutExpireDialog(this,errorResponse.getError().getData().getMessage());
//                            break;
//                        default:
//                            new LogoutExpireDialog(this,errorResponse.getError().getData().getMessage());
//                            break;
//                    }



                    // error case
              /*      switch (response.code()) {
                        case 400:

                            break;
                        case 401:


                            LoginService loginService =
                                    ServiceGenerator.createService(LoginService.class, MySharedPreferences.getHeaderUsername(), MySharedPreferences.getHeaderPassword());
                            final MySharedPreferences preferences=new MySharedPreferences(context);
                            Call<JsonObject> callss = loginService.getRefeshRoken(new RefeshTokenRequest(preferences.getAccessToken(),"refresh_token",preferences.getRefeshToken()));

                            callss.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    Log.i(TAG, "onResponse: " + response);

                                    if (response.isSuccessful()) {

                                        if (response.body().getAsJsonObject("success") != null) {

                                            Gson gson = new GsonBuilder().setLenient().create();
                                            SessionReponse json = gson.fromJson(response.body().getAsJsonObject("success").getAsJsonObject("data"), SessionReponse.class);

                                            preferences.setAccessToken(json.getResult().getSession().getAccess_token());
                                            preferences.setRefeshToken(json.getResult().getSession().getRefresh_token());
                                            preferences.setTokenType(json.getResult().getSession().getToken_type());
                                            preferences.setExpiresIn(json.getResult().getSession().getExpires_in());

                                            //getDetails();
                                        }else
                                        {

                                        }
                                    }
                                    else {
                                        // error case
                                        switch (response.code()) {
                                            case 400:
                                                new LogoutExpireDialog(context,"Refresh token has expired");

                                                break;
                                            case 401:
                                                break;
                                            case 403:
                                                break;
                                            default:
                                                break;
                                        }


                                    }


                                    //loading.cancel();
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Log.e(TAG, t.toString());
                                    //new RequestTimeoutDialog(context);
                                    loading.cancel();
                                }
                            });


                            break;
                        case 403:

                            break;
                        default:
                            break;
                    }
*/
                }



                //     loading.cancel();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, t.toString());
                //loading.cancel();
            }
        });
    }
}
