package nodomain.freeyourgadget.gadgetbridge.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

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
        //Intent intent = new Intent(this,)
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("GBGPSService", "onStartCommand executed");
        showAccelerometer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("GBGPSService", "onDestroy executed");
    }


    public void showAccelerometer()//button的监听事件
    {
        sm=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //count++;
        //tv.setText(count+"");
        listener=new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                // TODO Auto-generated method stub
                float xyz[]=event.values;
                //获得加速器三个方位x,y,z的数值集合
                float x=xyz[0];
                float y=xyz[1];
                float z=xyz[2];
                if(Math.abs(x-mx)>20||Math.abs(y-my)>20||Math.abs(z-mz)>20)//mx是相对于之前比较的数据。
                {
                    String s = new String(x+" "+y+" "+z);
                    Log.i("showAccelerometer", s);
                }
                mx=x;
                my=y;
                mz=z;
                float m = mx*mx + my*my + mz*mz;

                /*try {
                    sleep(5000);
                } catch (Exception e) {

                }*/
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        };
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

}
