package fr.castorflex.android.quickanswer.pojos;

import android.telephony.SmsMessage;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 25/08/12
 * Time: 04:38
 * To change this template use File | Settings | File Templates.
 */
public class Message {

    private String sender;
    private String message;

    public Message(String sender, SmsMessage... messages)
    {
        this.sender = sender;
        message = "";

        for(int i = 0 ; i < messages.length ; ++i)
        {
            message += messages[i].getDisplayMessageBody();
        }
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
