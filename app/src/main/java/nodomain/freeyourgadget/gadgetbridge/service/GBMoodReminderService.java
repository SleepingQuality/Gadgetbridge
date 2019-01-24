package nodomain.freeyourgadget.gadgetbridge.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import nodomain.freeyourgadget.gadgetbridge.activities.MoodReminderActivity;
import nodomain.freeyourgadget.gadgetbridge.service.receivers.GBMoodResultReceiver;

public class GBMoodReminderService extends Service {

    private Context context = this;

    private GBMoodResultReceiver gbMoodResultReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;

    private static int HOUR = 3600000;//10000 for debug, 3600000 for release
    private static int START_REMINDER = 1;
    private static int END_REMINDER = 15;//24 for debug, 15 for release

    private static float mood_x = 0;
    private static float mood_y = 0;

    private long lastTimeMillis;
    private long nowTimeMillis;

    private Vibrator vibrator;

    private Thread thread;

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
        mood_x = 0; mood_y = 0;
        lastTimeMillis = 0;
        nowTimeMillis = 0;

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("nodomain.freeyourgadget.gadgetbridge.GBMoodResult");
        gbMoodResultReceiver = new GBMoodResultReceiver();
        localBroadcastManager.registerReceiver(gbMoodResultReceiver, intentFilter);
        //registerReceiver(gbMoodResultReceiver, intentFilter);
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
        localBroadcastManager.unregisterReceiver(gbMoodResultReceiver);
        //unregisterReceiver(gbMoodResultReceiver);
    }

    public static void setMood(float x, float y) {
        mood_x = x;
        mood_y = y;
    }

    public static float getMood_x() {return mood_x;}
    public static float getMood_y() {return mood_y;}

    public void startMoodReminder() {
        if (thread != null) {
            try {
                thread.stop();
                Log.i("startMoodReminder", "kill old thread.");
                thread = null;
            } catch (Exception e) {

            }
        }
        if (lastTimeMillis == 0) {
            lastTimeMillis = System.currentTimeMillis();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    nowTimeMillis = System.currentTimeMillis();
                    if (nowTimeMillis/(2*HOUR) - lastTimeMillis/(2*HOUR) >= 1 &&
                            (nowTimeMillis/HOUR)%24 >= START_REMINDER &&
                            (nowTimeMillis/HOUR)%24 <= END_REMINDER) {
                        vibrator=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        //vibrator.vibrate(400);//400ms

                        Intent showMoodReminder = new Intent(context, MoodReminderActivity.class);
                        showMoodReminder.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(showMoodReminder);

                        lastTimeMillis = nowTimeMillis;
                    }
                    //mood_x = gbMoodResultReceiver.mood_x;
                    //mood_y = gbMoodResultReceiver.mood_y;
                    //String str = new String("mood_x: " + mood_x + ", mood_y: " + mood_y);
                    //Log.d("MoodReminderService", str);
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {

                    }
                }
            }
        });
        thread.start();
    }
}
