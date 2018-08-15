package response;

import com.google.gson.annotations.SerializedName;

import util.Errors;

public class ErrorResponse {
   // @SerializedName("code")
   // private String message;
    //@SerializedName("error")
   // private Errors error;

    @SerializedName("error")
    private Errors error;

    public Errors getError() {
        return error;
    }

    public void setError(Errors error) {
        this.error = error;
    }
}
