package nodomain.freeyourgadget.gadgetbridge.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import nodomain.freeyourgadget.gadgetbridge.service.GBGPSService;

public class GBBootReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GBBootReceiver", "onReceive");
        Intent service = new Intent(context, GBGPSService.class);
        context.startService(service);
    }
}
