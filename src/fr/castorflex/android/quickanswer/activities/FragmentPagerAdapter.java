package fr.castorflex.android.quickanswer.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
public class FragmentPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private static final String TAG = "FragmentStatePagerAdapter";
    private static final boolean DEBUG = false;

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;

    public ArrayList<Fragment.SavedState> mSavedState = new ArrayList<Fragment.SavedState>();
    public ArrayList<MessageFragment> mFragments = new ArrayList<MessageFragment>();
    private Fragment mCurrentPrimaryItem = null;

    private HashMap<String, List<Message>> mData;
    private List<String> mSenders;
    private PopupActivity mActivity;

    private int mCurrentPosition;

    public FragmentPagerAdapter(PopupActivity activity, FragmentManager fm, HashMap<String, List<Message>> map, List<String> senders) {
        mFragmentManager = fm;
        mData = map;
        mSenders = senders;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return mSenders == null ? 0 : mSenders.size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public MessageFragment getItem(int position) {
        return new MessageFragment(mSenders.get(position), mData.get(mSenders.get(position)));
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

        myNotifyDataSetChanged(mSenders.indexOf(msg.getSender()));
        notifyDataSetChanged();
    }

    private void myNotifyDataSetChanged(int position) {
        try {
            MessageFragment f = null;
            if (position >= 0)
                f = mFragments.get(position);
            if (f == null) {
                f = getItem(mSenders == null ? 0 : mSenders.size());
                mFragments.set(position, f);
            }
            mFragments.get(position).notifyChanged();
            //mFragments.get(position).addSms(msg);
        } catch (Exception e) {
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // If we already have this item instantiated, there is nothing
        // to do. This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.

        // DONE Remove of the add process of the old stuff
        /* if (mFragments.size() > position) { Fragment f = mFragments.get(position); if (f != null) { return f; } } */

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        MessageFragment fragment = getItem(position);
        if (mSavedState.size() > position) {
            Fragment.SavedState fss = mSavedState.get(position);
            if (fss != null) {
                try // DONE: Try Catch
                {
                    fragment.setInitialSavedState(fss);
                } catch (Exception ex) {
                }
            }
        }
        while (mFragments.size() <= position) {
            mFragments.add(null);
        }
        fragment.setMenuVisibility(false);
        mFragments.set(position, fragment);
        mCurTransaction.add(container.getId(), fragment);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.remove(fragment);

        /*if (mCurTransaction == null) { mCurTransaction = mFragmentManager.beginTransaction(); } if (DEBUG) Log.v(TAG, "Removing item #" + position + ": f=" + object + " v=" + ((Fragment)
         * object).getView()); while (mSavedState.size() <= position) { mSavedState.add(null); } mSavedState.set(position, mFragmentManager.saveFragmentInstanceState(fragment));
         * mFragments.set(position, null); mCurTransaction.remove(fragment); */
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }


    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (mSavedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
            mSavedState.toArray(fss);
            state.putParcelableArray("states", fss);
        }
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment f = mFragments.get(i);
            if (f != null) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                mFragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            mSavedState.clear();
            mFragments.clear();
            if (fss != null) {
                for (int i = 0; i < fss.length; i++) {
                    mSavedState.add((Fragment.SavedState) fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key : keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    MessageFragment f = (MessageFragment) mFragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        while (mFragments.size() <= index) {
                            mFragments.add(null);
                        }
                        f.setMenuVisibility(false);
                        mFragments.set(index, f);
                    } else {
                        Log.w(TAG, "Bad fragment at key " + key);
                    }
                }
            }
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_UNCHANGED;
    }

    public void removeFragment(String fragment) {
        mSenders.remove(fragment);
        mData.remove(fragment);
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onPageSelected(int position) {
        try {
            mCurrentPosition = position;
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            mFragments.get(mCurrentPosition).hideKeyboard();
        } catch (Exception e) {
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
