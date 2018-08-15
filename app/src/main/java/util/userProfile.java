package util;

import com.google.gson.annotations.SerializedName;

public class userProfile {

    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;

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
}
