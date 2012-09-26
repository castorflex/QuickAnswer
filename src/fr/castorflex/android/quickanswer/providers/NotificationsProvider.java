package fr.castorflex.android.quickanswer.providers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import fr.castorflex.android.quickanswer.R;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 26/09/12
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
public class NotificationsProvider {

    private static NotificationsProvider instance = null;

    public static NotificationsProvider getInstance() {
        if (instance == null) {
            synchronized (NotificationsProvider.class) {
                if (instance == null) {
                    instance = new NotificationsProvider();
                }
            }
        }
        return instance;
    }

    private NotificationsProvider() {
    }

    public void notifySent(Context context) {
        final NotificationManager nm = getNotificationManager(context);

        final Notification notification = new Notification(
                R.drawable.ic_notif,
                context.getString(R.string.message_sent),
                System.currentTimeMillis());
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.setLatestEventInfo(context, "", "", null);
        nm.notify(2, notification);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nm.cancel(2);
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

        notification.setLatestEventInfo(context, "", "", null);
        nm.notify(1, notification);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nm.cancel(1);
            }
        }, 1000);
    }

    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    public void notifySmsReceived(Context context) {
        if (SettingsProvider.isNotifEnabled(context)) {
            if (SettingsProvider.isVibrateEnabled(context)) {
                if (!(((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode()
                        == AudioManager.RINGER_MODE_SILENT)) {
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = new long[]{0, 300, 200, 300, 200};
                    v.vibrate(pattern, -1);
                }
            }
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
