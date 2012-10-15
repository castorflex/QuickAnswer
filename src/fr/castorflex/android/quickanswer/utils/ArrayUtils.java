package fr.castorflex.android.quickanswer.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 18/09/12
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
public class ArrayUtils {

    public static <T> ArrayList<T> convertArrayToList(T[] array){
        ArrayList<T> ret = new ArrayList<T>(array.length);
        for(T t : array){
            ret.add(t);
        }

        return ret;
    }
}
