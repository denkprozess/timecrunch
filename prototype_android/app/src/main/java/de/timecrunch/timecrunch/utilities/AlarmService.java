package de.timecrunch.timecrunch.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.activities.TaskEditActivity;

public class AlarmService extends JobIntentService {
    private NotificationManager alarmNotificationManager;
    String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

    public AlarmService() {
        super();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        sendNotification("Hey, look into your TimeCrunch App!", intent);
    }

    private void sendNotification(String msg, Intent intent) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent activityStartIntent = new Intent(this, TaskEditActivity.class);
        activityStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String categoryId = intent.getStringExtra("CATEGORY_ID");
        activityStartIntent.putExtras(intent.getExtras());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityStartIntent
                , PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(
                this, NOTIFICATION_CHANNEL_ID).setContentTitle("Alarm").setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);


        alarmNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alarmNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }
}
