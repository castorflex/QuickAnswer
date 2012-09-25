package fr.castorflex.android.quickanswer.receivers;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import fr.castorflex.android.quickanswer.pojos.Message;
import fr.castorflex.android.quickanswer.providers.SettingsProvider;
import fr.castorflex.android.quickanswer.ui.popup.PopupActivity;

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

    private final static int MESSAGE_OK = 0;
    private final static int MESSAGE_FAILED = 1;

    public final static String SMS_SENT = "com.android.mms.transaction.MESSAGE_SENT";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public static final String SMS_PACKAGE = "com.android.mms";
    public static final String SMS_RECEIVER = SMS_PACKAGE + ".transaction.SmsReceiver";

    private PhoneStateListener mListener;
    private ArrayList<Message> mMessageList;
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
        }

        if (SettingsProvider.isAppEnabled(context)) {
            if (action.equals(SMS_RECEIVED)) {
                TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                addSMSToStack(intent.getExtras());
                if (mgr.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
                    notifyPopup(context);
                } else {
                    if (mListener == null) {
                        initListener(mgr);
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
        }
    }

    private void notifyPopup(Context context) {
        Intent i = new Intent(context, PopupActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putParcelableArrayListExtra("listpdus", mMessageList);
        context.startActivity(i);
        mMessageList.clear();
    }

    private void addSMSToStack(Bundle extras) {
        Object[] pdus = (Object[]) extras.get("pdus");
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; ++i) {
            byte[] byteData = (byte[]) pdus[i];
            messages[i] = SmsMessage.createFromPdu(byteData);
        }
        if (mMessageList == null)
            mMessageList = new ArrayList<Message>();
        mMessageList.add(new Message(messages[0].getDisplayOriginatingAddress(), messages));
    }

    private void initListener(TelephonyManager mgr) {
        mListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mMessageList != null && mMessageList.size() > 0) {
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
        Intent intent = mIntent.setClassName(
                SMS_PACKAGE,
                SMS_RECEIVER);

        List<ResolveInfo> ri = mContext.getPackageManager().queryBroadcastReceivers(intent, 0);
        try {
            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, 0);
            pi.send(mResultCode);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
