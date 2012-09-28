package fr.castorflex.android.quickanswer.providers;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.castorflex.android.quickanswer.pojos.Message;
import fr.castorflex.android.quickanswer.utils.ArrayUtils;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 28/09/12
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
public class JSONProvider {

    private static final String KEY_MESSAGES = "messages";


    public static ArrayList<Message> getStoredMessages(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);

        String str = prefs.getString(KEY_MESSAGES, null);
        if (str == null)
            return null;

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        Message[] msgs = gson.fromJson(str, Message[].class);

        return ArrayUtils.convertArrayToList(msgs);
    }

    public static void storeMessage(Context context, Message msg) {
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);

        ArrayList<Message> list = getStoredMessages(context);
        if (list == null)
            list = new ArrayList<Message>();
        list.add(msg);

        String str = gson.toJson(list.toArray(), Message[].class);

        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(KEY_MESSAGES, str);
        edit.commit();
    }

    public static void clearMessagesList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_MESSAGES, null).commit();
    }
}
