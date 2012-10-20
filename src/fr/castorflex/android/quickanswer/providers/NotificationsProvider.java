package fr.castorflex.android.quickanswer.providers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.pojos.Message;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 26/09/12
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
public abstract class NotificationsProvider {

    private static int NOTIF_RECEIVED = 1;
    private static int NOTIF_SENT = 2;
    private static int NOTIF_SENDING = 3;


    private static NotificationsProvider instance = null;

    public static NotificationsProvider getInstance() {
        if (instance == null) {
            synchronized (NotificationsProvider.class) {
                if (instance == null) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                        instance = new NotificationsProviderV7();
                    else
                        instance = new NotificationsProviderV11();
                }
            }
        }
        return instance;
    }

    public abstract void notifySent(Context context);

    public abstract void notifySending(Context context);

    public abstract void notifySmsReceived(Context context);

    public abstract void clearReceived(Context context);


    public static class NotificationsProviderV7 extends NotificationsProvider {

        public NotificationsProviderV7() {
        }

        public void clearReceived(Context context) {
            final NotificationManager nm = getNotificationManager(context);
            nm.cancel(NOTIF_RECEIVED);
        }

        private NotificationManager getNotificationManager(Context context) {
            return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        public void notifySent(Context context) {
            final NotificationManager nm = getNotificationManager(context);

            final Notification notification = new Notification(
                    R.drawable.ic_notif,
                    context.getString(R.string.message_sent),
                    System.currentTimeMillis());
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            Intent notificationIntent = new Intent("fr.castorflex.android.quickanswer.action.unknownIntent");
            PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);

            notification.setLatestEventInfo(context, "", "", contentIntent);
            nm.notify(NOTIF_SENT, notification);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nm.cancel(NOTIF_SENT);
                }
            }, 1000);
        }

        public void notifySending(Context context) {
            final NotificationManager nm = getNotificationManager(context);

            final Notification notification = new Notification(
                    R.drawable.ic_notif,
                    context.getString(R.string.message_sending),
                    System.currentTimeMillis());
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            Intent notificationIntent = new Intent("fr.castorflex.android.quickanswer.action.unknownIntent");
            PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);

            notification.setLatestEventInfo(context, "", "", contentIntent);
            nm.notify(NOTIF_SENDING, notification);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nm.cancel(NOTIF_SENDING);
                }
            }, 1000);
        }


        public void notifySmsReceived(Context context) {
            if (SettingsProvider.isNotifEnabled(context)) {
                //statusbar
                List<Message> list = MessageProvider.getStoredMessages(context);
                int nb = list == null ? 0 : list.size();

                if (nb == 0)
                    return;

                String contentStr = nb == 1 ? context.getString(R.string.new_message_1) :
                        String.format(context.getString(R.string.new_message_x), nb);

                final NotificationManager nm = getNotificationManager(context);

                final Notification notification = new Notification(
                        R.drawable.ic_notif,
                        context.getString(R.string.message_received),
                        System.currentTimeMillis());

                Intent notificationIntent = new Intent("fr.castorflex.android.quickanswer.action.unknownIntent");
                PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);

                notification.setLatestEventInfo(context, context.getString(
                        R.string.message_received), contentStr, contentIntent);
                nm.notify(NOTIF_RECEIVED, notification);

                //vibrate
                if (SettingsProvider.isVibrateEnabled(context)) {
                    if (!(((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode()
                            == AudioManager.RINGER_MODE_SILENT)) {
                        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        long[] pattern = new long[]{0, 300, 200, 300, 200};
                        v.vibrate(pattern, -1);
                    }
                }

                //ringtone
                if (SettingsProvider.isRingtoneEnabled(context)) {
                    try {
                        Ringtone r = RingtoneManager.getRingtone(context, SettingsProvider.getRingtoneUri(context));
                        r.play();
                    } catch (Exception e) {
                    }
                }
            }
        }


    }

    public static class NotificationsProviderV11 extends NotificationsProvider {

        public NotificationsProviderV11() {
        }

        public void clearReceived(Context context) {
            final NotificationManager nm = getNotificationManager(context);
            nm.cancel(NOTIF_RECEIVED);
        }

        private NotificationManager getNotificationManager(Context context) {
            return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        public void notifySent(Context context) {

            Intent notificationIntent = new Intent("fr.castorflex.android.quickanswer.action.unknownIntent");
            PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);


            Notification notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                    .setContentTitle(context.getText(R.string.message_sent))
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .getNotification();


            final NotificationManager nm = getNotificationManager(context);
            nm.notify(NOTIF_SENT, notification);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nm.cancel(NOTIF_SENT);
                }
            }, 1000);
        }

        public void notifySending(Context context) {
            final NotificationManager nm = getNotificationManager(context);
            Intent notificationIntent = new Intent("fr.castorflex.android.quickanswer.action.unknownIntent");
            PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);


            Notification notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                    .setContentTitle(context.getText(R.string.message_sending))
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .getNotification();

            nm.notify(NOTIF_SENDING, notification);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nm.cancel(NOTIF_SENDING);
                }
            }, 1000);
        }


        public void notifySmsReceived(Context context) {
            if (SettingsProvider.isNotifEnabled(context)) {
                //statusbar
                List<Message> list = MessageProvider.getStoredMessages(context);
                int nb = list == null ? 0 : list.size();

                if (nb == 0)
                    return;

                String contentStr = nb == 1 ? context.getString(R.string.new_message_1) :
                        String.format(context.getString(R.string.new_message_x), nb);

                final NotificationManager nm = getNotificationManager(context);
                Intent notificationIntent = new Intent("fr.castorflex.android.quickanswer.action.unknownIntent");
                PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);

                Notification notification = new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_notif)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                        .setContentTitle(context.getText(R.string.message_received))
                        .setContentText(contentStr)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .getNotification();


                nm.notify(NOTIF_RECEIVED, notification);

                //vibrate
                if (SettingsProvider.isVibrateEnabled(context)) {
                    if (!(((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode()
                            == AudioManager.RINGER_MODE_SILENT)) {
                        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        long[] pattern = new long[]{0, 300, 200, 300, 200};
                        v.vibrate(pattern, -1);
                    }
                }

                //ringtone
                if (SettingsProvider.isRingtoneEnabled(context)) {
                    try {
                        Ringtone r = RingtoneManager.getRingtone(context, SettingsProvider.getRingtoneUri(context));
                        r.play();
                    } catch (Exception e) {
                    }
                }
            }
        }


    }


}
