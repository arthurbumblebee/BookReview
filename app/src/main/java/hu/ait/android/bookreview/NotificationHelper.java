package hu.ait.android.bookreview;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;


public class NotificationHelper extends ContextWrapper {
    private NotificationManager mNotificationManager;
    public static final String NEW_POST_CHANNEL = "new post";
    public static final String REPLY_TO_POST_CHANNEL = "reply to post";

    public NotificationHelper(Context context) {
        super(context);

        NotificationChannel newPostChannel =
                new NotificationChannel(
                        NEW_POST_CHANNEL,
                        getString(R.string.notification_channel_new_post),
                        NotificationManager.IMPORTANCE_DEFAULT);

        newPostChannel.setLightColor(Color.GREEN);
        newPostChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 500, 200, 500});
        newPostChannel.setShowBadge(false);

        getNotificationManager().createNotificationChannel(newPostChannel);

        NotificationChannel replyToPostChannel =
                new NotificationChannel(
                        REPLY_TO_POST_CHANNEL,
                        getString(R.string.notification_channel_reply_to_post),
                        NotificationManager.IMPORTANCE_HIGH);

        replyToPostChannel.setLightColor(Color.BLUE);
        replyToPostChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 500, 200, 500});
        replyToPostChannel.setShowBadge(true);


        getNotificationManager().createNotificationChannel(replyToPostChannel);

    }

    public Notification.Builder getNotificationNewPost(String title, String body) {
        return new Notification.Builder(getApplicationContext(), NEW_POST_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getAppSmallIcon())
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent());
    }

    public Notification.Builder getNotificationReply(String title, String body) {
        return new Notification.Builder(getApplicationContext(), REPLY_TO_POST_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getAppSmallIcon())
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent openMainIntent = new Intent(this, ReviewRequestsActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ReviewRequestsActivity.class);
        stackBuilder.addNextIntent(openMainIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
    }

    public void notify(int notificationId, Notification.Builder notification) {
        getNotificationManager().notify(notificationId, notification.build());
    }

    private int getAppSmallIcon() {
        return android.R.drawable.stat_notify_chat;
    }

    private NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

}
