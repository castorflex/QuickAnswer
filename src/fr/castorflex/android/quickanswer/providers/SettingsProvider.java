package fr.castorflex.android.quickanswer.providers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.castorflex.android.quickanswer.pojos.QuickAnswer;
import fr.castorflex.android.quickanswer.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 17/09/12
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */
public class SettingsProvider {

    public final static QuickAnswer[] DEFAULT_QA = new QuickAnswer[]{
            new QuickAnswer("Yes"),
            new QuickAnswer("No"),
            new QuickAnswer("See You"),
            new QuickAnswer("OK"),
            new QuickAnswer("Kiss")};

    private static final String KEY_ENABLED = "enabled";
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
        return context.getSharedPreferences(context.getApplicationInfo().packageName, Context.MODE_PRIVATE);
    }

    public static boolean isAppEnabled(Context context) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(KEY_ENABLED, true);
    }

    public static void setAppEnabled(Context context, boolean value)
    {
        SharedPreferences prefs = getSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(KEY_ENABLED, value);
        edit.commit();
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
            ret = ArrayUtils.convertArrayToList(DEFAULT_QA);
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


}
