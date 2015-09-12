package com.example.reico_000.prescriptionreadernfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by reico_000 on 20/3/2015.
 */
public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent arg1) {
        // For our recurring task, we'll just display a message
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        Log.d("Autostart", "BOOT_COMPLETED broadcast received. Executing starter service.");

        Intent intent = new Intent(context, StarterService.class);
        context.startService(intent);


    }
}
