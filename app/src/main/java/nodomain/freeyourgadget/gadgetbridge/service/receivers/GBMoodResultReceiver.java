package nodomain.freeyourgadget.gadgetbridge.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import nodomain.freeyourgadget.gadgetbridge.service.GBMoodReminderService;

public class GBMoodResultReceiver extends BroadcastReceiver {

    public float mood_x = 0;
    public float mood_y = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GBMoodResultReceiver", "onReceive executed");
        mood_x = intent.getFloatExtra("mood_x", mood_x);
        mood_y = intent.getFloatExtra("mood_y", mood_y);
        GBMoodReminderService.setMood(mood_x, mood_y);
        String str = new String("mood_x: " + mood_x + ", mood_y: " + mood_y);
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}
