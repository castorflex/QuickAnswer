package fr.castorflex.android.quickanswer.providers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.pojos.QuickAnswer;
import fr.castorflex.android.quickanswer.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 17/09/12
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */
public class SettingsProvider {

    private static final String KEY_QA = "qa";


    public static String getApplicationVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return "0.0";
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isAppEnabled(Context context) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_qa_activate), true);
    }

    public static ArrayList<QuickAnswer> getQuickAnswers(Context context) {
        SharedPreferences prefs = getSharedPreferences(context);
        String strAnswers = prefs.getString(KEY_QA, null);
        ArrayList<QuickAnswer> ret = null;
        if (strAnswers != null && strAnswers.length() > 0) {
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();
            QuickAnswer[] answers = gson.fromJson(strAnswers, QuickAnswer[].class);

            ret = ArrayUtils.convertArrayToList(answers);
        } else {
            String[] strs = context.getResources().getStringArray(R.array.array_default_qa);
            ret = new ArrayList<QuickAnswer>(strs.length);
            for (String str : strs) {
                ret.add(new QuickAnswer(str));
            }
        }
        return ret;
    }

    public static void setQuickAnswers(Context context, List<QuickAnswer> list) {
        SharedPreferences prefs = getSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        String answers = gson.toJson(list.toArray(), QuickAnswer[].class);

        editor.putString(KEY_QA, answers);
        editor.commit();
    }

    public static boolean isNotifEnabled(Context context) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_notif_activate), false);
    }

    public static boolean isVibrateEnabled(Context context) {
        if (!isAppEnabled(context))
            return false;
        if (!isNotifEnabled(context))
            return false;

        return getSharedPreferences(context).getBoolean(
                context.getString(R.string.pref_key_notif_vibrate), false);
    }

    public static boolean isRingtoneEnabled(Context context) {
        if (!isAppEnabled(context))
            return false;
        if (!isNotifEnabled(context))
            return false;
        String ringtone = getSharedPreferences(context).getString(
                context.getString(R.string.pref_key_notif_ringtone), "");
        return ringtone != null && ringtone.length() > 0;
    }


    public static Uri getRingtoneUri(Context context) {
        SharedPreferences prefs = getSharedPreferences(context);
        return Uri.parse(prefs.getString(context.getString(R.string.pref_key_notif_ringtone), ""));
    }

    public static String getRingtoneName(Context context) {
        Uri uri = getRingtoneUri(context);
        return getRingtoneName(context, uri);
    }

    public static String getRingtoneName(Context context, String str) {
        return getRingtoneName(context, Uri.parse(str));
    }

    public static String getRingtoneName(Context context, Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        if (uri.toString().length() > 0)
            return ringtone.getTitle(context);
        return context.getString(R.string.silent);
    }
}
