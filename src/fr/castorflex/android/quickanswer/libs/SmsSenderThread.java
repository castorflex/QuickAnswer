package fr.castorflex.android.quickanswer.libs;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import fr.castorflex.android.quickanswer.pojos.Message;
import fr.castorflex.android.quickanswer.receivers.SMSReceiver;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 25/08/12
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class SmsSenderThread extends Thread {



    public SmsSenderThread(final Context c, final Message msg) {
        super(new Runnable() {
            @Override
            public void run() {

                SmsManager smsManager = SmsManager.getDefault();
                if (smsManager != null) {
                    Uri uri = addSms(c, msg);
                    ArrayList<String> mess = smsManager.divideMessage(msg.getMessage());
                    ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
                    for (String msg : mess) {
                        sentIntents.add(PendingIntent.getBroadcast(c, 0,
                                new Intent(SMSReceiver.SMS_SENT, uri).
                                        setClass(c, SMSReceiver.class),
                                0));
                    }
                    smsManager.sendMultipartTextMessage(msg.getSender(), null, mess, sentIntents, null);

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
