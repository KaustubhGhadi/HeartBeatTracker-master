package in.programmeraki.hbt.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import in.programmeraki.hbt.LiveActivity;
import in.programmeraki.hbt.R;
import in.programmeraki.hbt.model.TrackerAlert;

public class NotificationBuilder {

    private static NotificationInterface notificationInterface;

    /*method for building notification*/
    public static void showNotification(TrackerAlert trackerAlert, int id, Context appContext) {
        int notiff_icon = R.mipmap.ic_launcher;

        if(LiveActivity.isAlive){
            notificationInterface.showNotification(trackerAlert);
            return;
        }

        try {
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    appContext,
                    id,
                    new Intent(),
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            /*build our notification*/
            NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext);

            /*create notification*/
            Notification mNotification = builder.setSmallIcon(notiff_icon)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(appContext.getResources().getString(R.string.critical_alert_str))
                    .setContentText(trackerAlert.getMsg())
                    .setLargeIcon(BitmapFactory.decodeResource(appContext.getResources(), notiff_icon))
                    .build();

            /*notification flag for cancel notification automatically*/
            mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

            /*create notification manager to notify user*/
            NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);

            /*notify the user*/
            notificationManager.notify(id, mNotification);
            Log.d("tmp", "done showing notification");
            playNotificationSound(appContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Playing notification sound
    private static void playNotificationSound(Context c) {
        try {
            final Uri notiffSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(c, notiffSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clears notification tray messages
    private static void clearNotifications(Context context) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setNotificationInterface(NotificationInterface para_notificationInterface) {
        notificationInterface = para_notificationInterface;
    }

}
