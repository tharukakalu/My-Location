package api;

import com.google.gson.JsonObject;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import request.LoginRequest;
import request.SignUpRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiInterface {





    @POST
    Call<JsonObject> getData(@QueryMap Map<String, String> params, @Url String url);

  /*  @GET("/apps/{id}/technologies")
    Observable<List<Technology>> getTechnologies(@Path("id") String id);*/
//http://cseller.com/dskkgca8wniquwo2/buyer-signin-api
    @Multipart
    @POST("buyer-signin-api")
    Call<JsonObject> doLogin(@Part("email") RequestBody email,
                             @Part("password") RequestBody password);

    @Multipart
    @POST("user-signup")
    Call<JsonObject> signUp(@Part("name") RequestBody name,
                            @Part("email") RequestBody email,
                            @Part("password") RequestBody password,
                            @Part("reEnterPassword") RequestBody reEnterPassword);


}