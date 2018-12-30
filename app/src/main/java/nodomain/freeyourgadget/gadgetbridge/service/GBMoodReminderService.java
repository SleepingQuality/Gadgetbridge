package nodomain.freeyourgadget.gadgetbridge.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import nodomain.freeyourgadget.gadgetbridge.activities.MoodReminderActivity;

public class GBMoodReminderService extends Service {

    private Context context = this;

    private static int HOUR = 3600000;
    private static int START_REMINDER = 1;
    private static int END_REMINDER = 14;

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
                    if (nowTimeMillis/HOUR - lastTimeMillis/HOUR >= 2 &&
                            (nowTimeMillis/HOUR)%24 >= START_REMINDER &&
                            (nowTimeMillis/HOUR)%24 <= END_REMINDER) {
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
