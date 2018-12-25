package nodomain.freeyourgadget.gadgetbridge.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import nodomain.freeyourgadget.gadgetbridge.activities.MoodReminderActivity;

public class GBMoodReminderService extends Service {
    Context context = this;

    public GBMoodReminderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("GBMoodReminderService", "onCreate executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("GBMoodReminderService", "onStartCommand executed");
        startMoodReminder();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("GBMoodReminderService", "onDestroy executed");
    }

    public void startMoodReminder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long lastTimeMillis = System.currentTimeMillis();
                long nowTimeMillis;
                Vibrator vibrator;
                while (true) {
                    nowTimeMillis = System.currentTimeMillis();
                    if (nowTimeMillis/7200000 - lastTimeMillis/7200000 >= 1 &&
                            (nowTimeMillis/3600000)%24 >= 1 &&  (nowTimeMillis/3600000)%24 <= 14) {
                        vibrator=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(400);//400ms

                        Intent showMoodReminder = new Intent(context, MoodReminderActivity.class);
                        showMoodReminder.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(showMoodReminder);

                        lastTimeMillis = nowTimeMillis;
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }
}
