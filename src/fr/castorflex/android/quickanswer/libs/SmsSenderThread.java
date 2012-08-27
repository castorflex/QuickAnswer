package fr.castorflex.android.quickanswer.libs;

import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 25/08/12
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class SmsSenderThread extends Thread {

    private static final int MAX_CHAR = 160;

    public SmsSenderThread(final String phoneNumber, final String message) {
        super(new Runnable() {
            @Override
            public void run() {

                SmsManager smsManager = SmsManager.getDefault();
                if (smsManager != null) {
                        ArrayList<String> mess = smsManager.divideMessage(message);
                        smsManager.sendMultipartTextMessage(phoneNumber, null, mess, null, null);

                }
            }
        });
    }


}
