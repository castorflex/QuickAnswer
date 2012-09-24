package fr.castorflex.android.quickanswer.providers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.pojos.Contact;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 25/08/12
 * Time: 04:00
 * To change this template use File | Settings | File Templates.
 */
public abstract class ContactProvider {

    private static ContactProvider instance = null;

    public static ContactProvider getInstance() {
        if (instance == null) {
            synchronized (ContactProvider.class) {
                if (instance == null) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                        instance = new ContactProviderV7();
                    else
                        instance = new ContactProviderV11();
                }
            }
        }
        return instance;
    }


    public abstract Contact getContact(final String phoneNumber, Context context);


    public static class ContactProviderV7 extends ContactProvider {

        private ContactProviderV7() {
        }

        @Override
        public Contact getContact(String phoneNumber, Context context) {
            ContentResolver resolver = context.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            Cursor c = resolver.query(uri, new String[]{
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.NUMBER,
                    ContactsContract.PhoneLookup._ID}, null, null, null);

            Contact contact = null;
            if (c != null && c.getCount() > 0) {
                if (c.moveToFirst()) {
                    Uri photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                            c.getLong(2));
                    contact = new Contact(c.getString(0), c.getString(1), photoUri.toString());
                }
            } else {
                contact = new Contact(context.getString(R.string.unknown), phoneNumber, null);
            }
            c.close();
            c = null;
            return contact;
        }
    }

    public static class ContactProviderV11 extends ContactProvider {

        private ContactProviderV11() {
        }

        @Override
        public Contact getContact(String phoneNumber, Context context) {
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
}
