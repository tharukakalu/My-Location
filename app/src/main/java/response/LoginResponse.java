package response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import util.userProfile;

public class LoginResponse {

    @SerializedName("userId")
    private String userId;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("userProfile")
    private userProfile userProfile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public util.userProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(util.userProfile userProfile) {
        this.userProfile = userProfile;
    }
}
