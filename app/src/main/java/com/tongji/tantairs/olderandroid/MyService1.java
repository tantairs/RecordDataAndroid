package com.tongji.tantairs.olderandroid;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MyService1 extends Service {
    String name = "XIAOMI-ANDROID";
    String amount;
    String time;
    boolean onOrOff;
    String id;
    WebUtil webUtil = new WebUtil();
    private LocationManager locationManager;
    private String locationProvider;
    MyLocationListener locationListener;
    private static final String TAG = "GpsService";

    BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            int scale = intent.getIntExtra("scale", 100);
            amount = ((level * 100) / scale) + "%";
        }
    };

    @Override
    public void onCreate() {
        this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        myStartService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    GpsStatus.Listener listener = new GpsStatus.Listener() {

        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "第一次定位");
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i(TAG, "卫星状态改变");
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int account = 0;
                    while (iters.hasNext() && account <= maxSatellites) {
                        iters.next();
                        account++;
                    }
                    Log.v(TAG, "GPS is **unusable**　" + account + "     " + maxSatellites);
                    if (account < 3) {
                        Log.i(TAG, "the number of satellite is " + account);
                    } else if (account > 7) {
                        Log.i(TAG, "the number of satellite is " + account);
                    }
                    System.out.println("搜索到： " + account + " 颗卫星");
                    break;
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "定位启动");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "定位结束");
                    break;
                default:
                    break;
            }
        }
    };

    public void myStartService() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        locationProvider = locationManager.getBestProvider(criteria, true);
        locationListener = new MyLocationListener();
        locationManager.addGpsStatusListener(listener);
        locationManager.requestLocationUpdates(locationProvider, 3000, 0, locationListener);
    }

    private class MyLocationListener implements LocationListener {

        ArrayList<String> tmp = new ArrayList<String>();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String filename = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) + ".csv";
        private FileOutputStream out;
        private File file;
        double a, b;

        @Override
        public void onLocationChanged(Location location) {
            a = location.getLongitude();
            b = location.getLatitude();
            time = sDateFormat.format(new Date());
            onOrOff = getScreenOnOffData();
            id = getIdData();
            tmp.add(name);
            tmp.add(id);
            tmp.add(sDateFormat.format(new java.util.Date()));
            tmp.add(String.valueOf(location.getLongitude()));
            tmp.add(String.valueOf(location.getLatitude()));
            tmp.add(String.valueOf(onOrOff));
            tmp.add(String.valueOf(amount));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    webUtil.postToDataSource(name, id, time, a, b, "200", onOrOff, amount);
                }
            }).start();

            if (tmp.size() > 63) {
                saveData(tmp);
                tmp.clear();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.i("TAG", "当前GPS状态为可见状态");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i("TAG", "当前GPS状态为服务区外状态");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        public boolean getScreenOnOffData() {
            try {
                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                boolean result = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && powerManager.isInteractive() || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH && powerManager.isScreenOn();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        public String getIdData() {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String equid = tm.getDeviceId();
            return equid;
        }

        public void saveData(ArrayList<String> list) {

            FileUtil fileUtil = new FileUtil();
            fileUtil.creatSDDir("TrackData1");
            file = fileUtil.createSDFile(filename);

            try {
                out = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < list.size(); i += 7) {
                try {
                    out.write(list.get(i).getBytes());
                    out.write(",".getBytes());
                    out.write(list.get(i + 1).getBytes());
                    out.write(",".getBytes());
                    out.write(list.get(i + 2).getBytes());
                    out.write(",".getBytes());
                    out.write(list.get(i + 3).getBytes());
                    out.write(",".getBytes());
                    out.write(list.get(i + 4).getBytes());
                    out.write(",".getBytes());
                    out.write(list.get(i + 5).getBytes());
                    out.write(",".getBytes());
                    out.write(list.get(i + 6).getBytes());
                    out.write('\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
