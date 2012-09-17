package fr.castorflex.android.quickanswer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import fr.castorflex.android.quickanswer.activities.PopupActivity;
import fr.castorflex.android.quickanswer.pojos.Message;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 22/08/12
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
public class SMSReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private PhoneStateListener mListener;
    private ArrayList<Message> mMessageList;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String action = intent.getAction();
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
                        if(mMessageList != null && mMessageList.size() > 0){
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
}
