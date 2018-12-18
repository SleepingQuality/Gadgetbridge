package nodomain.freeyourgadget.gadgetbridge.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GBGPSService extends Service {
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
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("GBGPSService", "onDestroy executed");
    }

}
