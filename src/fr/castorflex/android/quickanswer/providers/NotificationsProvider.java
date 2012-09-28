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
import fr.castorflex.android.quickanswer.pojos.Message;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 26/09/12
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
public class NotificationsProvider {

    private static int NOTIF_RECEIVED = 1;
    private static int NOTIF_SENT = 2;
    private static int NOTIF_SENDING = 3;


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

        notification.setLatestEventInfo(context, "", "", null);
        nm.notify(NOTIF_SENDING, notification);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nm.cancel(NOTIF_SENDING);
            }
        }, 1000);
    }

    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    public void notifySmsReceived(Context context) {
        if (SettingsProvider.isNotifEnabled(context)) {
            //statusbar
            List<Message> list = JSONProvider.getStoredMessages(context);
            int nb = list == null ? 0 : list.size();

            if(nb == 0)
                return;

            String contentStr = nb == 1 ? context.getString(R.string.new_message_1) :
                    String.format(context.getString(R.string.new_message_x), nb);

            final NotificationManager nm = getNotificationManager(context);

            final Notification notification = new Notification(
                    R.drawable.ic_notif,
                    context.getString(R.string.message_received),
                    System.currentTimeMillis());

            notification.setLatestEventInfo(context, context.getString(
                    R.string.message_received), contentStr, null);
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

    public void clearReceived(Context context) {
        final NotificationManager nm = getNotificationManager(context);

        nm.cancel(NOTIF_RECEIVED);
    }
}
