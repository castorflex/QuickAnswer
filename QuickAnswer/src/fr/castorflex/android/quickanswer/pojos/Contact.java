package fr.castorflex.android.quickanswer.pojos;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 25/08/12
 * Time: 04:08
 * To change this template use File | Settings | File Templates.
 */
public class Contact {
    private String name;
    private String number;
    private String photo;

    public Contact(String name, String number, String photo) {
        this.name = name;
        this.number = number;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getPhoto() {
        return photo;
    }
}
