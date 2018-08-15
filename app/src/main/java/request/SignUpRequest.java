package request;

import com.google.gson.annotations.SerializedName;

public class SignUpRequest {

    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("reEnterPassword")
    private String reEnterPassword;
    @SerializedName("password")
    private String password;

    public SignUpRequest(String email, String password,String name,String reEnterPassword) {
        this.email = email;
        this.password = password;
        this.reEnterPassword = reEnterPassword;
        this.name = name;

    }

}
