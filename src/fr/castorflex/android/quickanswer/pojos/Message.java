package fr.castorflex.android.quickanswer.pojos;

import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.SmsMessage;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 25/08/12
 * Time: 04:38
 * To change this template use File | Settings | File Templates.
 */
public class Message implements Parcelable {

    private String sender;
    private String message;
    private Date date;

    public Message(String sender, SmsMessage... messages) {
        this.sender = sender;
        message = "";

        for (int i = 0; i < messages.length; ++i) {
            message += messages[i].getDisplayMessageBody();
        }
        date = Calendar.getInstance().getTime();
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    //PARCELABLE
    @Override
    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(sender);
        parcel.writeValue(message);
        parcel.writeValue(date);
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(
                    in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public Message(Parcel parcel) {
        ClassLoader cl = getClass().getClassLoader();
        sender = (String) parcel.readValue(cl);
        message = (String) parcel.readValue(cl);
        date = (Date) parcel.readValue(cl);
    }
}
