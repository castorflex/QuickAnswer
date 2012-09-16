package fr.castorflex.android.quickanswer.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import fr.castorflex.android.quickanswer.libs.FragmentStatePagerAdapter;
import fr.castorflex.android.quickanswer.pojos.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 25/08/12
 * Time: 00:56
 * To change this template use File | Settings | File Templates.
 */
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

    private HashMap<String, List<Message>> mData;
    private List<String> mSenders;
    private PopupActivity mActivity;

    private int mCurrentPosition;


    public MyFragmentPagerAdapter(PopupActivity activity, FragmentManager fm, HashMap<String, List<Message>> map, List<String> senders) {
        super(fm);
        mData = map;
        mSenders = senders;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return mSenders == null ? 0 : mSenders.size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public MessageFragment getItem(int position) {
        return new MessageFragment(mSenders.get(position), mData.get(mSenders.get(position)),
                position > 0, position < mSenders.size() - 1
        );
    }

    @Override
    public void startUpdate(ViewGroup container) {
    }

    public void addSmsMessage(Message msg) {
        if (!mData.containsKey(msg.getSender())) {
            mData.put(msg.getSender(), new ArrayList<Message>());
            mSenders.add(msg.getSender());
        }
        mData.get(msg.getSender()).add(msg);

        notifyDataSetChanged();

    }

    private void notifyPrevNext(int position) {
        //prev
//        for (int i = position - 1; i >= 0; --i) {
//            MessageFragment prev = (MessageFragment) mFragments.get(i);
//            if (prev != null) {
//                prev.notifyNext();
//            }
//        }

        //next
//        for (int i = position + 1; i < mFragments.size(); ++i) {
//            MessageFragment next = (MessageFragment) mFragments.get(i);
//            if (next != null) {
//                next.notifyPrev();
//            }
//        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void removeFragment(String fragment) {
        mSenders.remove(fragment);
        mData.remove(fragment);
    }

    public String getCurrentSender() {
        return mSenders.get(mCurrentPosition);
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int position) {
        try {
            mCurrentPosition = position;
            mActivity.hideKeyboard();
            mActivity.clearEditText();
        } catch (Exception e) {
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }
}
