package fr.castorflex.android.quickanswer.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.SmsMessage;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.utils.MeasuresUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 22/08/12
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */
public class PopupActivity extends FragmentActivity {


    private static final int HEIGHT_P = MeasuresUtils.DpToPx(350);
    private static final int HEIGHT_L = MeasuresUtils.DpToPx(215);


    private ViewPager mViewPager;
    private FragmentPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        setContentView(R.layout.popup_layout);
        setFinishOnTouchOutside(false);


        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), new HashMap<String, List<SmsMessage>>(), new ArrayList<String>());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageMargin(5);
        mViewPager.setPageMarginDrawable(R.drawable.viewpager_margin);
        updateScreenSize();

        populateAdapterFromBundle(getIntent().getExtras());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);    //To change body of overridden methods use File | Settings | File Templates.
        populateAdapterFromBundle(intent.getExtras());

    }

    private void populateAdapterFromBundle(Bundle b) {
        List<String> ret = new ArrayList<String>();
        Object[] pdus = (Object[]) b.get("pdus");
        for (int i = 0; i < pdus.length; ++i) {
            byte[] byteData = (byte[]) pdus[i];
            SmsMessage msg = SmsMessage.createFromPdu(byteData);
            mPagerAdapter.addSmsMessage(msg);
        }
    }

    private void updateScreenSize() {
        LayoutParams params = getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        if (MeasuresUtils.getScreenOrientation(this) == MeasuresUtils.Orientation.Portrait) {
            mViewPager.getLayoutParams().height = HEIGHT_P;
        } else {
            mViewPager.getLayoutParams().height = HEIGHT_L;
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        updateScreenSize();
        super.onConfigurationChanged(newConfig);

    }


}
