package fr.castorflex.android.quickanswer.receivers;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.*;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.pojos.Message;
import fr.castorflex.android.quickanswer.providers.MMSProvider;
import fr.castorflex.android.quickanswer.providers.MessageProvider;
import fr.castorflex.android.quickanswer.providers.NotificationsProvider;
import fr.castorflex.android.quickanswer.providers.SettingsProvider;
import fr.castorflex.android.quickanswer.ui.popup.PopupActivity;
import fr.castorflex.android.quickanswer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 22/08/12
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
public class SMSReceiver extends BroadcastReceiver {

    public final static String EXTRA_URI = "extra_uri";

    private final static int MESSAGE_OK = 0;
    private final static int MESSAGE_FAILED = 1;

    public final static String SMS_SENT = "com.android.mms.transaction.MESSAGE_SENT";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";


    public static final String SMS_PACKAGE = "com.android.mms";
    public static final String SMS_RECEIVER = SMS_PACKAGE + ".transaction.SmsReceiver";
    public static final String SMS_SAMSUNG_MESSAGING_COMPOSE_CLASS_NAME =
            "com.android.mms.ui.ConversationComposer";

    /**
     * The type of the message
     * <P>Type: INTEGER</P>
     */
    public static final String TYPE = "type";

    public static final int MESSAGE_TYPE_ALL = 0;
    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;
    public static final int MESSAGE_TYPE_DRAFT = 3;
    public static final int MESSAGE_TYPE_OUTBOX = 4;
    public static final int MESSAGE_TYPE_FAILED = 5; // for failed outgoing messages
    public static final int MESSAGE_TYPE_QUEUED = 6; // for messages to send later

    private PhoneStateListener mListener;
    private Context mContext;
    private Intent mIntent;

    private int mResultCode;


    private Handler smsSentHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_OK:
                    notifySmsSent(true);
                    break;
                case MESSAGE_FAILED:
                    notifySmsSent(false);
                    break;
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        mIntent = intent;
        mContext = context;
        String action = intent.getAction();

        if (action.equals(SMS_SENT)) {
            notifySmsSentAction(context, intent);
        } else if (action.equals(Intent.ACTION_USER_PRESENT)) {
            notifyPopup(context);
        } else if (SettingsProvider.isAppEnabled(context)) {
            if (action.equals(SMS_RECEIVED) || action.equals(MMS_RECEIVED)) {
                if (action.equals(MMS_RECEIVED))
                    addMMSToStack();
                else
                    addSMSToStack(context, intent.getExtras());

                TelephonyManager mgr = (TelephonyManager)
                        context.getSystemService(Context.TELEPHONY_SERVICE);

                if (mgr.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
                    NotificationsProvider.getInstance().notifySmsReceived(context);
                    notifyPopup(context);
                } else {
                    if (mListener == null) {
                        initListener(mgr, context);
                    }
                }
            }
        }
    }

    private void notifySmsSentAction(Context context, Intent intent) {
        android.os.Message msg = smsSentHandler.obtainMessage();
        mResultCode = getResultCode();
        switch (mResultCode) {
            case Activity.RESULT_OK:
                NotificationsProvider.getInstance().notifySent(context);
                msg.what = MESSAGE_OK;
                smsSentHandler.sendMessage(msg);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
            case SmsManager.RESULT_ERROR_NO_SERVICE:
            case SmsManager.RESULT_ERROR_NULL_PDU:
            case SmsManager.RESULT_ERROR_RADIO_OFF:
            default:
                msg.what = MESSAGE_FAILED;
                smsSentHandler.sendMessage(msg);
                Toast.makeText(mContext, R.string.sending_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void notifyPopup(final Context context) {

        if (!Utils.isLocked(context)) {
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    NotificationsProvider.getInstance().clearReceived(context);
                }
            }, 1000);

            ArrayList<Message> messageArrayList = MessageProvider.getStoredMessages(context);
            if (messageArrayList != null && messageArrayList.size() > 0) {
                Intent i = new Intent(context, PopupActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putParcelableArrayListExtra("listpdus", messageArrayList);
                context.startActivity(i);
                MessageProvider.clearMessagesList(context);
            }
        }
    }

    private void addMMSToStack() {
        Thread.currentThread();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Message message = MMSProvider.getLastMMS(mContext);
        if (message != null) {
            message.setType(Message.TYPE_MMS);
            MessageProvider.storeMessage(mContext, message);
        }
    }

    private void addSMSToStack(Context context, Bundle extras) {
        Object[] pdus = (Object[]) extras.get("pdus");
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; ++i) {
            byte[] byteData = (byte[]) pdus[i];
            messages[i] = SmsMessage.createFromPdu(byteData);
        }
        MessageProvider.storeMessage(context,
                new Message(messages[0].getDisplayOriginatingAddress(), messages));
    }

    private void initListener(TelephonyManager mgr, final Context context) {
        mListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        ArrayList<Message> list = MessageProvider.getStoredMessages(context);
                        if (list != null && list.size() > 0) {
                            NotificationsProvider.getInstance().notifySmsReceived(context);
                            notifyPopup(mContext);
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        break;
                }
            }
        };
        mgr.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void notifySmsSent(boolean success) {
        boolean noAppFound = false;
        Intent intent = mIntent.setClassName(
                SMS_PACKAGE,
                SMS_RECEIVER);

        Uri uri = Uri.parse(intent.getStringExtra(EXTRA_URI));


        List<ResolveInfo> ri = mContext.getPackageManager().queryBroadcastReceivers(intent, 0);
        if (ri.size() > 0) {
            ////////////////////////////////////////////////////////////////
            // Thanks Adam K for this "hack"
            // Only the samsung sms/mms apk has this modified compose class
            final Intent samsungIntent = new Intent();
            samsungIntent.setClassName(
                    SMS_PACKAGE,
                    SMS_SAMSUNG_MESSAGING_COMPOSE_CLASS_NAME);
            ri = mContext.getPackageManager().queryIntentActivities(samsungIntent, 0);
            if (ri.size() > 0) {
                // no stock system app found to finish the message move
                noAppFound = true;
            }
            ////////////////////////////////////////////////////////////////
        } else {
            noAppFound = true;
        }

        if (noAppFound) {
            ContentValues cv = new ContentValues(7);

            if (!success) {
                cv.put(TYPE, MESSAGE_TYPE_FAILED);
                cv.put("read", 0);
            } else {
                cv.put(TYPE, SMSReceiver.MESSAGE_TYPE_SENT);
                cv.put("read", 1);
            }
            ContentResolver resolver = mContext.getContentResolver();
            resolver.update(uri, cv, null, null);

        } else {
            try {
                PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, 0);
                pi.send(mResultCode);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }
}
