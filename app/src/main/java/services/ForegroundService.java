package services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nimesh.macbook.mylocation.MainActivity;
import com.nimesh.macbook.mylocation.R;
import android.location.LocationListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import util.Constants;

public class ForegroundService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String LOG_TAG = "ForegroundService";
    public static boolean IS_SERVICE_RUNNING = false;
    private String userId;
    Timer timer;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude,longitude;
    LocationManager locationManager;
    Location location;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
   // private final Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            showNotification();
            Toast.makeText(this, "Service Started!", Toast.LENGTH_SHORT).show();

        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Log.i(LOG_TAG, "Clicked Previous");

            Toast.makeText(this, "Clicked Previous!", Toast.LENGTH_SHORT)
                    .show();
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Log.i(LOG_TAG, "Clicked Play");

            Toast.makeText(this, "Clicked Play!", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Log.i(LOG_TAG, "Clicked Next");

            Toast.makeText(this, "Clicked Next!", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }


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
                            mFirebaseDatabase.child(userId).child("lastupdateDate").setValue(formattedDate);

                          //   Toast.makeText(getApplicationContext(), formattedDate+" Service Started ", Toast.LENGTH_LONG).show();

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

        return START_STICKY;
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, ForegroundService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, ForegroundService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, ForegroundService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.common_full_open_on_phone);

        Notification notification = new NotificationCompat.Builder(this).build();
//                .setContentTitle("TutorialsFace Music Player")
//                .setTicker("TutorialsFace Music Player")
//                .setContentText("My song")
//                .setSmallIcon(R.drawable.common_full_open_on_phone)
//                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
//                .setContentIntent(pendingIntent)
//                .setOngoing(true)
//                .addAction(android.R.drawable.ic_media_previous, "Previous",
//                        ppreviousIntent)
//                .addAction(android.R.drawable.ic_media_play, "Play",
//                        pplayIntent)
//                .addAction(android.R.drawable.ic_media_next, "Next",
//                        pnextIntent).build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);

//        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancelAll();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
        Toast.makeText(this, "Service Detroyed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case if services are bound (Bound Services).
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

       // Toast.makeText(this, "Service!", Toast.LENGTH_SHORT).show();
        if (location != null) {
           // Log.d(TAG, "== location != null");

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            mFirebaseDatabase.child(userId).child("lat").setValue( location.getLatitude());
            mFirebaseDatabase.child(userId).child("lng").setValue( location.getLongitude());
            mFirebaseDatabase.child(userId).child("lastupdate").setValue(formattedDate);

            //Send result to activities
            //  sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
          //  Toast.makeText(getApplicationContext(), "Location "+ String.valueOf(location.getLatitude())+" "+ String.valueOf(location.getLongitude()), Toast.LENGTH_LONG).show();
        }
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
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,5, (android.location.LocationListener) this);
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,5, (android.location.LocationListener) this);
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
}