package com.nimesh.macbook.mylocation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import api.ApiClient;
import api.ApiInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import firebase.Regester;
import firebase.User;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import request.LoginRequest;
import request.SignUpRequest;
import response.ErrorResponse;
import response.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name) EditText _nameText;
  //  @BindView(R.id.input_address) EditText _addressText;
    @BindView(R.id.input_email) EditText _emailText;
  //  @BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
  //  private static final String TAG = MainActivity.class.getSimpleName();
  SharedPreferences pref;
    SharedPreferences.Editor editor;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

       // _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
       // String address = _addressText.getText().toString();
        final String email = _emailText.getText().toString();
       // String mobile = _mobileText.getText().toString();
        final  String password = _passwordText.getText().toString();
        final  String reEnterPassword = _reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                      //  onSignupSuccess(name,email,password,reEnterPassword);
                        // onSignupFailed();
                        doSignUp();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess(String name,String email,String password,String reEnterPassword) {


        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Realtime Database");

        // app_title change listener
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);

                // update toolbar title
               // getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });


        createUser(name,email, password,reEnterPassword);

        _signupButton.setEnabled(true);
     //   setResult(RESULT_OK, null);
      //  finish();
    }


    /**
     * Creating new user node under 'users'
     */
    private void createUser(String name,String email,String password,String reEnterPassword) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
//        if (TextUtils.isEmpty(userId)) {
//            userId = mFirebaseDatabase.push().getKey();
//        }

        Regester user = new Regester(name, email,password,reEnterPassword);

        mFirebaseDatabase.child(name).setValue(user);

        addUserChangeListener(name);
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener(String name) {
        // User data change listener
        mFirebaseDatabase.child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.email + ", " + user.password);

                // Display newly updated name and email
                //usertxt.setText(user.name + ", " + user.password);

                // clear edit text
                // inputEmail.setText("");
                //inputName.setText("");

              //  loginSusess();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    public void onLoginSuccess(String userName,String password,String userId)
    {
        editor.putBoolean("isLog", true); // Storing boolean - true/false
        editor.putString("Username",  userName); // Storing string
        editor.putString("Password", password); // Storing string  userId
        editor.putString("userId",  userId);
        editor.commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
      //  String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
      //  String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

//        if (address.isEmpty()) {
//            _addressText.setError("Enter Valid Address");
//            valid = false;
//        } else {
//            _addressText.setError(null);
//        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }
//
//        if (mobile.isEmpty() || mobile.length()!=10) {
//            _mobileText.setError("Enter Valid Mobile Number");
//            valid = false;
//        } else {
//            _mobileText.setError(null);
//        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }


    private void doSignUp() {

           ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
//        LoginService apiService =
//                ServiceGenerator.createService(LoginService.class, MySharedPreferences.getHeaderUsername(), MySharedPreferences.getHeaderPassword());


        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), _nameText.getText().toString());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), _emailText.getText().toString());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), _passwordText.getText().toString());
        RequestBody reEnterPassword = RequestBody.create(MediaType.parse("text/plain"), _reEnterPasswordText.getText().toString());

        Call<JsonObject> call = apiService.signUp(name,email, password,reEnterPassword);

        //   Call<JsonObject> call = apiService.doLogin(new LoginRequest(email.getText().toString(), password.getText().toString(), FirebaseInstanceId.getInstance().getToken(), "android", "2815b8158cbb1bcca4b2cfb33459af0d",android_id));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {

                    Log.i(TAG, "onResponse: " + response.body());
                    if (response.body().getAsJsonObject("data") != null) {

                        Gson gson = new GsonBuilder().setLenient().create();
                        LoginResponse json = gson.fromJson(response.body().getAsJsonObject("data"), LoginResponse.class);

                        onLoginSuccess(json.getName(),json.getEmail(),json.getUserId());



                    } else {
                        if (response.body().getAsJsonObject("error") != null) {
                         //   setSuccessDialog(0, response.body().getAsJsonObject("error").getAsJsonObject("data").get("message").toString().replace("\"",""));
                            Log.i(TAG, "onResponse: " + response.body().getAsJsonObject("error"));
                        }
                    }
                }else {


                    Gson gson = new Gson();
                    ErrorResponse errorResponse = gson.fromJson(
                            response.errorBody().charStream(),
                            ErrorResponse.class);
                    Toast.makeText(getApplicationContext(), "Sorry! Email already exist" , Toast.LENGTH_SHORT).show();

                    // setSuccessDialog(0, errorResponse.getError().getData().getMessage());

// [text={"status":"false","message":"<p>Email already exist<\\/p>"}]
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