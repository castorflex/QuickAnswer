package fr.castorflex.android.quickanswer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import fr.castorflex.android.quickanswer.SettingsActivity;
import fr.castorflex.android.quickanswer.activities.PopupActivity;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 22/08/12
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
public class SMSReceiver extends BroadcastReceiver{

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(SMS_RECEIVED)){
            Intent i = new Intent(context, PopupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtras(intent.getExtras());
            context.startActivity(i);
        }
    }
}
