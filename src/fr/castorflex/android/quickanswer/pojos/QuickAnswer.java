package fr.castorflex.android.quickanswer.pojos;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 16/09/12
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class QuickAnswer {
    private String mMessage;

    public QuickAnswer(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }
}
