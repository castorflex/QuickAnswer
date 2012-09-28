package fr.castorflex.android.quickanswer.utils;

import android.app.KeyguardManager;
import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 27/09/12
 * Time: 18:45
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    public static boolean isLocked(Context context) {
        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (myKM.inKeyguardRestrictedInputMode()) {
            return true;
        } else {
            return false;
        }
    }
}
