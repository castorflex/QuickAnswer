package fr.castorflex.android.quickanswer.libs;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.telephony.SmsManager;
import fr.castorflex.android.quickanswer.pojos.Message;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 25/08/12
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class SmsSenderThread extends Thread {

    private static final int MAX_CHAR = 160;

    public SmsSenderThread(final Context c,final Message msg) {
        super(new Runnable() {
            @Override
            public void run() {

                SmsManager smsManager = SmsManager.getDefault();
                if (smsManager != null) {
                    Uri uri = addSms(c, msg);
                    ArrayList<String> mess = smsManager.divideMessage(msg.getMessage());
                    smsManager.sendMultipartTextMessage(msg.getSender(), null, mess, null, null);

                }
            }
        });
    }

    public static Uri addSms(Context context, Message msg) {

        Uri uri = Uri.parse("content://sms/outbox");

        ContentValues cv = new ContentValues(7);
        cv.put("address", msg.getSender());
        cv.put("body", msg.getMessage());
        cv.put("date", msg.getDate().getTime());

        ContentResolver resolver = context.getContentResolver();
        return resolver.insert(uri, cv);
    }


}
