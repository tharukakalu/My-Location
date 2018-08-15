package util;

import com.google.gson.annotations.SerializedName;


public class Errors {

    @SerializedName("code")
    private String code;

    @SerializedName("data")
    private Data data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
