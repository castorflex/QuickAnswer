package fr.castorflex.android.quickanswer.providers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import fr.castorflex.android.quickanswer.pojos.Contact;
import fr.castorflex.android.quickanswer.pojos.Message;

import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 09/10/12
 * Time: 18:56
 * To change this template use File | Settings | File Templates.
 */
public class MMSProvider {

    public static final Uri MMS_CONTENT_URI = Uri.parse("content://mms");
    public static final String MMS_ADDR_CONTENT_URI_STRING = "content://mms/{0}/addr";


    public static Message getLastMMS(Context context) {

        Message message = null;

        //1st step: get id
        final String[] projection = new String[]{"_id", "date"};
        final String selection = "read=0";
        final String order = "date ASC";

        Cursor cursor = context.getContentResolver().query(
                Uri.withAppendedPath(MMS_CONTENT_URI, "inbox"),
                projection,
                selection,
                null,
                order);

        long id = 0;
        try {
            if (cursor != null && cursor.moveToLast()) {
                id = cursor.getLong(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            cursor = null;
        }

        if (id > 0) {

            //2nd step: get addr
            final String selectionAddr = "msg_id=" + id;

            Uri uriAddr = Uri.parse(MessageFormat.format(MMS_ADDR_CONTENT_URI_STRING, id));
            Cursor cAdd = context.getContentResolver().query(uriAddr, null,
                    selectionAddr, null, null);

            try {
                if (cAdd != null && cAdd.moveToFirst()) {
                    long contact_id = 0L;
                    try {
                        contact_id = Long.parseLong(cAdd.getString(cAdd.getColumnIndex("contact_id")));
                    } catch (Exception e) {
                    }
                    String address = cAdd.getString(cAdd.getColumnIndex("address"));

                    if (address != null) {
                        message = new Message(address, null, Message.TYPE_MMS);
                    }
                    if (contact_id > 0) {
                        Contact contact = ContactProvider.getInstance().getContactById(contact_id, context);
                        if (message != null)
                            message.setContact(contact);
                        else {
                            if (contact != null)
                                message = new Message(contact.getNumber(), null, Message.TYPE_MMS);
                        }
                    }

                }
            } catch (Exception e) {

            } finally {
                try {
                    cAdd.close();
                } catch (Exception e) {
                }
            }

        }

        return message;
    }
}
