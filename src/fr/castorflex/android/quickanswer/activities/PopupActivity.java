package fr.castorflex.android.quickanswer.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.SmsMessage;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.libs.FixedSpeedScroller;
import fr.castorflex.android.quickanswer.pojos.Message;
import fr.castorflex.android.quickanswer.utils.MeasuresUtils;

import java.lang.reflect.Field;
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


    private static final int HEIGHT_P = MeasuresUtils.DpToPx(48 + 44 + 130 + 48);
    private static final int HEIGHT_L = MeasuresUtils.DpToPx(215);


    private ViewPager mViewPager;
    private FragmentPagerAdapter mPagerAdapter;
    private FixedSpeedScroller mNewScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        setContentView(R.layout.popup_layout);
        setFinishOnTouchOutside(false);


        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerAdapter = new FragmentPagerAdapter(this, getSupportFragmentManager(), new HashMap<String, List<Message>>(), new ArrayList<String>());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageMargin(5);
        mViewPager.setPageMarginDrawable(R.drawable.viewpager_margin);
        mViewPager.setOnPageChangeListener(mPagerAdapter);
        try {
            Field scroller;
            scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            mNewScroller = new FixedSpeedScroller(this, new DecelerateInterpolator());
            scroller.set(mViewPager, mNewScroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

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
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; ++i) {
            byte[] byteData = (byte[]) pdus[i];
            messages[i] = SmsMessage.createFromPdu(byteData);
        }
        mPagerAdapter.addSmsMessage(new Message(messages[0].getDisplayOriginatingAddress(), messages));
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


    public void removeFragment(final String fragment) {
        if (mPagerAdapter.getCount() <= 1)
            finish();
        else {
            if (mPagerAdapter.getCurrentPosition() == mPagerAdapter.getCount() - 1){
                mViewPager.setCurrentItem(mPagerAdapter.getCurrentPosition() - 1);
            }
            else
                mViewPager.setCurrentItem(mPagerAdapter.getCurrentPosition() + 1);
            Handler h = new Handler(getMainLooper());
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPagerAdapter.removeFragment(fragment);
                    mPagerAdapter.notifyDataSetChanged();
                }
            }, mNewScroller.getScrollerDuration());

        }

    }
}
