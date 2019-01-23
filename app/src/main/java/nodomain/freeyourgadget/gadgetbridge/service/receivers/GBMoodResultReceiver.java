package nodomain.freeyourgadget.gadgetbridge.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class GBMoodResultReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GBMoodResultReceiver", "onReceive executed");
        Toast.makeText(context, "GBMoodResultReceiver: onReceive", Toast.LENGTH_LONG).show();
    }
}
