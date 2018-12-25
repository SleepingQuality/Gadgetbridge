package nodomain.freeyourgadget.gadgetbridge.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import nodomain.freeyourgadget.gadgetbridge.R;

import static java.lang.Math.sqrt;

public class GBGPSService extends Service {
    private Sensor sensor;
    private SensorManager sm;
    private SensorEventListener listener;
    private float mx,my,mz;

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
        Log.d("GBGPSService", "onCreate executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("GBGPSService", "onStartCommand executed");

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

        startAccelerometer();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("GBGPSService", "onDestroy executed");
        sm.unregisterListener(listener);
        Intent gbGPSService = new Intent(this, GBGPSService.class);
        this.startService(gbGPSService);
    }


    public void startAccelerometer()
    {
        sm=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        listener=new SensorEventListener() {

            long lastTimeMillis = System.currentTimeMillis();
            long nowTimeMillis = System.currentTimeMillis();

            @Override
            public void onSensorChanged(SensorEvent event) {
                // TODO Auto-generated method stub
                nowTimeMillis = System.currentTimeMillis();
                float xyz[]=event.values;
                //获得加速器三个方位x,y,z的数值集合
                float x=xyz[0];
                float y=xyz[1];
                float z=xyz[2];
                if(nowTimeMillis/60000 - lastTimeMillis/60000 >= 1)
                {
                    String s = new String(x+" "+y+" "+z+"--"+nowTimeMillis);
                    Log.i("showAccelerometer", s);
                    lastTimeMillis = nowTimeMillis;
                }
                mx=x;
                my=y;
                mz=z;
                float m = mx*mx + my*my + mz*mz;


            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        };
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

}
