package util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.google.gson.JsonObject;

import retrofit2.Callback;



public class LogoutExpireDialog extends AlertDialog.Builder {


    public LogoutExpireDialog(@NonNull final Context context, String msg) {
        super(context);
        try{

            this.setTitle("Alert");
            this.setMessage(msg);
            this.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                  //  CommonMethod.doLogout(context);

                }
            });

            this.setCancelable(false);
            //  this.setIcon(R.drawable.ic_logo);
            this.show();
        /*    if(!this.isShowing()){
                //if its visibility is not showing then show here
                this.show();
            }else{
                //do something here... if already showing
            }*/
        }catch(Exception s)
        {
            s.printStackTrace();
        }

    }


}
