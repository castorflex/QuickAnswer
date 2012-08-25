package fr.castorflex.android.quickanswer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class MeasuresUtils {

    public static enum Orientation {
        Portrait, Landscape
    }

    public static int DpToPx(int dp) {
        Resources r = Resources.getSystem();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, r.getDisplayMetrics());
        return px;
    }

    public static float PxToSp(float px) {
        Resources r = Resources.getSystem();
        float scaledDensity = r.getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public static float SpToPx(float sp) {
        Resources r = Resources.getSystem();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, r.getDisplayMetrics());
        return px;
    }

    public static Orientation getScreenOrientation(Context c) {
        Display display = ((WindowManager) c.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        if (metrics.widthPixels > metrics.heightPixels) {
            return Orientation.Landscape;
        } else {
            return Orientation.Portrait;
        }
    }
}
