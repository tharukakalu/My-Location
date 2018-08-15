package services;

/**
 * Created by solomoit on 9/15/2017.
 */

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GoogleService extends Service implements LocationListener {

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude,longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 1000;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private String userId;
    Timer timer;

    public GoogleService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        try{



            pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            editor = pref.edit();

            userId=pref.getString("userId", "");

            mFirebaseInstance = FirebaseDatabase.getInstance();

            // get reference to 'users' node
            mFirebaseDatabase = mFirebaseInstance.getReference("users");


            final Handler handler = new Handler();
            timer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {

                                Location lo=fn_getlocation();
                                Calendar c = Calendar.getInstance();

                                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                                String formattedDate = df.format(c.getTime());
                                mFirebaseDatabase.child(userId).child("lat").setValue( lo.getLatitude());
                                mFirebaseDatabase.child(userId).child("lng").setValue( lo.getLongitude());
                                mFirebaseDatabase.child(userId).child("lastupdate").setValue(formattedDate);

                                // Toast.makeText(getApplicationContext(), lo.getLongitude()+" Service Started "+lo.getLatitude(), Toast.LENGTH_LONG).show();

                               // mFirebaseDatabase.child(userId).setValue(user);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            timer.schedule(doAsynchronousTask, 0, 2000);

        }catch (Exception e)
        {
            e.printStackTrace();
        }



    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Mylocation.getLatitude" +location.getLatitude(), Toast.LENGTH_SHORT).show();
      //  Toast.makeText(this, "Mylocation.getAltitude" +location.getAltitude(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Mylocation.getLongitude" +location.getLongitude(), Toast.LENGTH_SHORT).show();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        mFirebaseDatabase.child(userId).child("lat").setValue( location.getLatitude());
        mFirebaseDatabase.child(userId).child("lng").setValue( location.getLongitude());
        mFirebaseDatabase.child(userId).child("lastupdate").setValue(formattedDate);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private Location fn_getlocation(){
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable){

        }else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
                if (isNetworkEnable){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){

                       // Log.e("latitude",location.getLatitude()+"");
                       // Log.e("longitude",location.getLongitude()+"");
                      //  Toast.makeText(getApplicationContext(), location.getLongitude()+"Service Started"+location.getLatitude(), Toast.LENGTH_LONG).show();

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                       // fn_update(location);
                    }
                }

            }


            if (isGPSEnable){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location!=null){
                       // Log.e("latitude",location.getLatitude()+"");
                       // Log.e("longitude",location.getLongitude()+"");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                      //  Toast.makeText(getApplicationContext(), location.getLongitude()+"Service Started"+location.getLatitude(), Toast.LENGTH_LONG).show();
                       // fn_update(location);
                    }
                }
            }


        }
            return location;
    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                }
            });

        }
    }




    private void fn_update(Location location){

        intent.putExtra("latutide",location.getLatitude()+"");
        intent.putExtra("longitude",location.getLongitude()+"");
        sendBroadcast(intent);
    }

    private void sendLoacation(Location location){

        intent.putExtra("latutide",location.getLatitude()+"");
        intent.putExtra("longitude",location.getLongitude()+"");
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        //timer.cancel();
       // Toast.makeText(this, "MyService   Stopped.", Toast.LENGTH_SHORT).show();
    }


}

