package services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service implements LocationListener {
    public int counter=0;
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
  //  Timer timer;
  private boolean isRunning  = false;

    public SensorService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        isRunning  = true;
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
//        Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
//        sendBroadcast(broadcastIntent);
        isRunning  = false;
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
       // timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {

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
                        Log.i("in timer", "in timer ++++  "+ (counter++));
                        Toast.makeText(getApplicationContext(), " Service Started "+(counter++), Toast.LENGTH_LONG).show();

                        if(isRunning)
                        {
                            try {

                                Location lo=fn_getlocation();
                                Calendar c = Calendar.getInstance();

                                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                                String formattedDate = df.format(c.getTime());
                                mFirebaseDatabase.child(userId).child("lat").setValue( lo.getLatitude());
                                mFirebaseDatabase.child(userId).child("lng").setValue( lo.getLongitude());
                                mFirebaseDatabase.child(userId).child("lastupdate").setValue(formattedDate);

                                Toast.makeText(getApplicationContext(), lo.getLongitude()+" Service Started "+lo.getLatitude(), Toast.LENGTH_LONG).show();
                              //  Log.i("in timer", "in timer ++++  "+ (counter++));
                              //  Toast.makeText(getApplicationContext(), " Service Started "+(counter++), Toast.LENGTH_LONG).show();

                                // mFirebaseDatabase.child(userId).setValue(user);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 2000);


    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    @Override
    public void onLocationChanged(Location location) {

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
}