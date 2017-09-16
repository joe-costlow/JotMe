package com.josephcostlow.jotme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Joseph Costlow on 02-Sep-17.
 */

public class ReminderNotificationJob extends JobService {

//    Create a notification, when clicked, launches the app.

    @Override
    public boolean onStartJob(JobParameters job) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Context context = getApplicationContext();

                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(getResources().getString(R.string.notification_context_title))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setContentText(getResources().getString(R.string.notification_context_text));

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notificationBuilder.build());
            }
        }).start();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }
}
