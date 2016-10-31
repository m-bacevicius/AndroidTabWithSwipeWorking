package inducesmile.com.androidtabwithswipe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Cube on 10/31/2016.
 */

public class AlertReceiver extends BroadcastReceiver{
    @Override
    public void onReceive (Context context, Intent intent)
    {
        createNotification(context, "Times Up", "5 Seconds Has Passed", "Alert");
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert)
    {
        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, new Intent(context, ChartTest.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setTicker(msgAlert)
                .setContentTitle(msg)
                .setContentText(msgText)
                .setSmallIcon(R.drawable.temperature);

        mBuilder.setContentIntent(notificationIntent);

        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
    }
}
