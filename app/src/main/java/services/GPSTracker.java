package services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Duke on 9/7/2015.
 */
public class GPSTracker extends Service implements LocationListener {
    private final Context mContext;


    boolean isGPSEnabled = false;


    boolean isNetworkEnabled = false;


    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2;


    private static final long MIN_TIME_BW_UPDATES = 2000;


    public int counter=0;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;

    LocationManager locationManager;

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
    private boolean isRunning  = false;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent reStart=new Intent(mContext,this.getClass());
        reStart.setPackage(getPackageName());
        startService(reStart);

        super.onTaskRemoved(rootIntent);
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);


            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);


            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                this.canGetLocation = true;
            //   if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        //    Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }

                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

       //     }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationManager.removeUpdates(GPSTracker.this);
        }
    }


    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }


    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }


        return longitude;
    }


    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);


        alertDialog.setTitle("Warning");


        alertDialog.setMessage("Please enabled GPS from settings");


        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        double lat = location.getLatitude();
        double longi = location.getLongitude();
       //  Log.i("My Location is \n" + lat + "\n" + longi);
        Log.e("longitude",location.getLongitude()+"");
        Log.e("getLatitude",location.getLatitude()+"");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        Log.e("formattedDate",formattedDate+"");
      //  Toast.makeText(getApplicationContext(), "My Location is \n" + lat + "\n" + longi, Toast.LENGTH_SHORT);
        getLocation();
        locationSave(location);

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    public void locationSave(Location location) {

        pref = mContext.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        userId=pref.getString("userId", "");

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");



                        try {


                            Calendar c = Calendar.getInstance();

                            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                            String formattedDate = df.format(c.getTime());
                            mFirebaseDatabase.child(userId).child("lat").setValue( location.getLatitude());
                            mFirebaseDatabase.child(userId).child("lng").setValue( location.getLongitude());
                            mFirebaseDatabase.child(userId).child("lastupdate").setValue(formattedDate);

                            Toast.makeText(mContext, " Service Started "+formattedDate +"lat" +location.getLatitude(), Toast.LENGTH_LONG).show();
                            //  Log.i("in timer", "in timer ++++  "+ (counter++));
                            //  Toast.makeText(getApplicationContext(), " Service Started "+(counter++), Toast.LENGTH_LONG).show();

                            // mFirebaseDatabase.child(userId).setValue(user);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }



    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        mFirebaseDatabase.child(userId).child("rebind").setValue(formattedDate);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        mFirebaseDatabase.child(userId).child("low").setValue(formattedDate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        mFirebaseDatabase.child(userId).child("stops").setValue(formattedDate);
    }



}
