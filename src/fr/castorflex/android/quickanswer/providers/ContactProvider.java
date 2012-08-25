package fr.castorflex.android.quickanswer.providers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import fr.castorflex.android.quickanswer.pojos.Contact;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 25/08/12
 * Time: 04:00
 * To change this template use File | Settings | File Templates.
 */
public class ContactProvider {

    public static Contact getContactName(final String phoneNumber, Context context) {

        ContentResolver resolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor c = resolver.query(uri, new String[]{
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NUMBER,
                ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI}, null, null, null);

        Contact contact = null;
        if (c.moveToFirst()) {
            contact = new Contact(c.getString(0), c.getString(1), c.getString(2));
        }
        c.close();
        c = null;
        return contact;
    }
}
