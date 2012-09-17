package fr.castorflex.android.quickanswer.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.SettingsActivity;
import fr.castorflex.android.quickanswer.libs.CustomViewPager;
import fr.castorflex.android.quickanswer.libs.FixedSpeedScroller;
import fr.castorflex.android.quickanswer.libs.OverflowLayout;
import fr.castorflex.android.quickanswer.libs.SmsSenderThread;
import fr.castorflex.android.quickanswer.pojos.Message;
import fr.castorflex.android.quickanswer.pojos.QuickAnswer;
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
public class PopupActivity extends FragmentActivity implements TextWatcher, View.OnClickListener, OverflowLayout.OnItemSelectedListener {


    private static final int HEIGHT_P = MeasuresUtils.DpToPx(48 + 44 + 130);
    private static final int HEIGHT_L = MeasuresUtils.DpToPx(48 + 44 + 70);


    private CustomViewPager mViewPager;
    private MyFragmentPagerAdapter mPagerAdapter;
    private FixedSpeedScroller mNewScroller;


    private ImageView mSmsAppButton;
    private ImageView mOverflowButton;
    private ImageView mSettingsButton;
    private OverflowLayout mOverflowMenu;

    private ImageView mSendButton;
    private EditText mEditTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        setContentView(R.layout.popup_layout);
        setFinishOnTouchOutside(false);

        mSettingsButton = (ImageView) findViewById(R.id.imageView_settings);
        mOverflowMenu = (OverflowLayout) findViewById(R.id.overflowmenu);
        mOverflowButton = (ImageView) findViewById(R.id.imageView_overflow);
        mSmsAppButton = (ImageView) findViewById(R.id.imageView_sms_app);
        mSendButton = (ImageView) findViewById(R.id.imageButton_send);
        mEditTextMessage = (EditText) findViewById(R.id.editText_message);
        mViewPager = (CustomViewPager) findViewById(R.id.viewPager);


        mPagerAdapter = new MyFragmentPagerAdapter(this, getSupportFragmentManager(), new HashMap<String, List<Message>>(), new ArrayList<String>());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageMargin(1);
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

        mEditTextMessage.addTextChangedListener(this);
        mSendButton.setClickable(false);
        mSendButton.setEnabled(false);
        mSendButton.setOnClickListener(this);
        mOverflowButton.setOnClickListener(this);
        mSettingsButton.setOnClickListener(this);

        mOverflowMenu.setOnItemSelectedListener(this);

        mSmsAppButton.setOnClickListener(this);

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

        List<Message> messages = b.getParcelableArrayList("listpdus");
        for(Message msg : messages){
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


    public void removeFragment(final String fragment) {
        if (mPagerAdapter.getCount() <= 1)
            finish();
        else {
            if (mPagerAdapter.getCurrentPosition() == mPagerAdapter.getCount() - 1) {
                mViewPager.setCurrentItem(mPagerAdapter.getCurrentPosition() - 1);
            } else
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

    //////////////////////IMPLEMENTS/////////////////////////////////////////////////
    @Override
    public void onClick(View view) {
        if (view == mSendButton) {
            hideKeyboard();

            String messageBody = mEditTextMessage.getText().toString();
            mEditTextMessage.setText("");
            new SmsSenderThread(mPagerAdapter.getCurrentSender(), messageBody).start();
            removeFragment(mPagerAdapter.getCurrentSender());
        } else if (view == mSmsAppButton) {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("address", mPagerAdapter.getCurrentSender());
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);
        } else if (view == mOverflowButton) {
            toggleOverflow();
        } else if(view == mSettingsButton){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }

    public void toggleOverflow() {
        mOverflowMenu.toggle();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSendButton.getWindowToken(), 0);
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditTextMessage, 0);
    }

    @Override
    public void onItemClick(QuickAnswer qa, int TYPE) {
        if (TYPE == OverflowLayout.TYPE_SEND){
            hideKeyboard();

            String messageBody = qa.getMessage();
            mEditTextMessage.setText("");
            new SmsSenderThread(mPagerAdapter.getCurrentSender(), messageBody).start();
            removeFragment(mPagerAdapter.getCurrentSender());
        }else if (TYPE == OverflowLayout.TYPE_EDIT){
            mEditTextMessage.setText(qa.getMessage());
            mEditTextMessage.requestFocus();
            mEditTextMessage.setSelection(qa.getMessage().length());
            showKeyboard();
        }
    }



    @Override
    public void onBackPressed() {
        if (!mOverflowMenu.onBackPressed())
            super.onBackPressed();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.toString() == null || editable.toString().length() == 0) {
            mSendButton.setEnabled(false);
            mSendButton.setClickable(false);
        } else {
            mSendButton.setEnabled(true);
            mSendButton.setClickable(true);
        }
    }

    public void clearEditText() {
        mEditTextMessage.setText("");
    }


    public void closeQuickAnswersMenu() {
        mOverflowMenu.close();
    }
}
