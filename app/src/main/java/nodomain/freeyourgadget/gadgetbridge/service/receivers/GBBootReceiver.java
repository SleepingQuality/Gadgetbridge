package nodomain.freeyourgadget.gadgetbridge.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import nodomain.freeyourgadget.gadgetbridge.service.GBGPSService;
import nodomain.freeyourgadget.gadgetbridge.service.GBMoodReminderService;
import nodomain.freeyourgadget.gadgetbridge.service.GBRecordManagementService;

public class GBBootReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GBBootReceiver", "onReceive");

        Intent gbGPSService = new Intent(context, GBGPSService.class);
        Intent gbMoodReminderService = new Intent(context, GBMoodReminderService.class);
        Intent gbRecordManagementService = new Intent(context, GBRecordManagementService.class);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(gbGPSService);
            context.startService(gbMoodReminderService);
            context.startService(gbRecordManagementService);
        } else {
            context.startForegroundService(gbGPSService);
            context.startForegroundService(gbMoodReminderService);
            context.startForegroundService(gbRecordManagementService);
        }
    }
}
