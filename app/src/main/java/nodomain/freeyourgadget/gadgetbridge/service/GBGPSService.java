package nodomain.freeyourgadget.gadgetbridge.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ServiceCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2;

import static java.lang.Math.sqrt;

public class GBGPSService extends Service {

    private Context context = this;

    static final String[] LOCATIONGPS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.INTERNET
    };
    private static int PERMISSION_CODE = 100;
    public static String CHANNEL_ID_STRING = "gb100003";

    private Sensor sensor;
    private SensorManager sm;
    private SensorEventListener listener;
    private double accele_x, accele_y, accele_z;

    private LocationClient mLocationClient = null;
    private MyLocationListener myListener;
    private LocationClientOption option;
    private double latitude, longitude, altitude, radius;

    private Thread initialThread;
    private Thread gpsThread;

    public GBGPSService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("GBGPSService", "onCreate executed");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID_STRING, "gadgetbridge", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING).build();
            startForeground(100003, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("GBGPSService", "onStartCommand executed");

        /* //to prevent the service from being killed
        Intent notificationIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Title")
                .setContentText("Message")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(0001,notification);
        */
        /*
        if (gpsThread != null) {
            try {
                if (mLocationClient != null) {

                    //mLocationClient.stop();
                }
                gpsThread.stop();
                Log.i("GBGPSService", "kill old gpsThread.");
                gpsThread = null;
            } catch (Exception e) {
                Log.e("GBGPSService", e.getMessage());
            }
        }
        if (initialThread != null) {
            try {
                initialThread.stop();
                Log.i("GBGPSService", "kill old initialThread.");
                initialThread = null;
            } catch (Exception e) {
                Log.e("GBGPSService", e.getMessage());
            }
        }
        */
        initialThread = new Thread(new Runnable() {
            @Override
            public void run() {
                startAccelerometer();
                startGPS();
            }
        });
        initialThread.start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("GBGPSService", "onDestroy executed");
        sm.unregisterListener(listener);

        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mLocationClient.unRegisterLocationListener(myListener);
    }

    public void startAccelerometer()
    {
        sm=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        listener=new SensorEventListener() {

            long lastTimeMillis = System.currentTimeMillis();
            long tempTimeMillis = System.currentTimeMillis();
            long nowTimeMillis = System.currentTimeMillis();
            float xsum = 0, ysum = 0, zsum = 0;
            int times = 0;

            @Override
            public void onSensorChanged(SensorEvent event) {
                // TODO Auto-generated method stub
                nowTimeMillis = System.currentTimeMillis();
                float xyz[]=event.values;
                //获得加速器三个方位x,y,z的数值集合
                float x=xyz[0];
                float y=xyz[1];
                float z=xyz[2];

                if (nowTimeMillis/5000 - tempTimeMillis/5000 >= 1) {
                    xsum += x*x;
                    ysum += y*y;
                    zsum += z*z;
                    String s = new String(x+" "+y+" "+z+" --"+nowTimeMillis);
                    Log.i("showAccelerometer", s);
                    tempTimeMillis = nowTimeMillis;
                    times ++;
                }
                if (nowTimeMillis/60000 - lastTimeMillis/60000 >= 1) {
                    accele_x = (sqrt(xsum/times));
                    accele_y = (sqrt(ysum/times));
                    accele_z = (sqrt(zsum/times));
                    String s = new String(accele_x+" "+accele_y+" "+accele_z+" --"+nowTimeMillis);
                    Log.i("showAccelerometer", s);
                    lastTimeMillis = nowTimeMillis;
                    xsum = 0;
                    ysum = 0;
                    zsum = 0;
                    times = 0;

                    //Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        };
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void startGPS() {
        mLocationClient = new LocationClient(getApplicationContext());
        myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);
        option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setIsNeedAltitude(true);
        mLocationClient.setLocOption(option);
        if (Build.VERSION.SDK_INT >= 23)
            getPermissions();

        gpsThread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        long lastTimeMillis = System.currentTimeMillis();
                        long nowTimeMillis;
                        while (true) {
                            nowTimeMillis = System.currentTimeMillis();
                            if (nowTimeMillis/60000 - lastTimeMillis/60000 >= 1) {
                                if (mLocationClient.isStarted()) {
                                    mLocationClient.stop();
                                }
                                mLocationClient.start();
                                lastTimeMillis = nowTimeMillis;
                            }
                            try {
                                Thread.sleep(5000);
                            } catch (Exception e) {

                            }
                            String locStr = new String(longitude+" "+latitude+" "+altitude+" "+radius+" "
                                    +" --"+System.currentTimeMillis());
                            Log.i("showGPS", locStr);

                        }
                    }
                }
        );
        gpsThread.start();
    }

    public void getPermissions() {
        String permissionStr;
        for (int i = 0; i < 8; i ++) {
            permissionStr = LOCATIONGPS[i];
            int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getApplication(), permissionStr);
            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                //Intent getPermissionActivity = new Intent(context, ControlCenterv2.class);
                //getPermissionActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //getApplication().startActivity(getPermissionActivity);
                break;
            } else {
                return;
            }
        }
        try {
            Thread.sleep(20000);
        } catch (Exception e) {

        }
        getPermissions();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            Log.d("onReceiveLocation", "executed");

            latitude = location.getLatitude();    //获取纬度信息
            longitude = location.getLongitude();    //获取经度信息
            altitude = location.getAltitude();
            radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

            //Toast.makeText(context, new String(longitude+" "+latitude+" "+altitude+" "+radius+" "
            //        +" --"+System.currentTimeMillis()), Toast.LENGTH_LONG).show();

        }
    }

    public double getAccele_x() {
        return accele_x;
    }

    public double getAccele_y() {
        return accele_y;
    }

    public double getAccele_z() {
        return accele_z;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getRadius() {
        return radius;
    }
}
