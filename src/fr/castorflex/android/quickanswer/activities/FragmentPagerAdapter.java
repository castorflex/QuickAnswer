package fr.castorflex.android.quickanswer.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import fr.castorflex.android.quickanswer.libs.CustomFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LogManager;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 25/08/12
 * Time: 00:56
 * To change this template use File | Settings | File Templates.
 */
public class FragmentPagerAdapter extends PagerAdapter {
    private static final String TAG = "FragmentStatePagerAdapter";
    private static final boolean DEBUG = false;

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;

    public ArrayList<Fragment.SavedState> mSavedState = new ArrayList<Fragment.SavedState>();
    public ArrayList<MessageFragment> mFragments = new ArrayList<MessageFragment>();
    private Fragment mCurrentPrimaryItem = null;

    HashMap<String, List<SmsMessage>> mData;
    List<String> mSenders;

    public FragmentPagerAdapter(FragmentManager fm, HashMap<String, List<SmsMessage>> map, List<String> senders) {
        mFragmentManager = fm;
        mData = map;
        mSenders = senders;
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

    public void addSmsMessage(SmsMessage msg) {
        if (!mData.containsKey(msg.getDisplayOriginatingAddress())) {
            mData.put(msg.getDisplayOriginatingAddress(), new ArrayList<SmsMessage>());
            mSenders.add(msg.getDisplayOriginatingAddress());
        }
        mData.get(msg.getDisplayOriginatingAddress()).add(msg);

        notifyDataSetChanged();
        myNotifyDataSetChanged(mSenders.indexOf(msg.getDisplayOriginatingAddress()), msg);
    }

    private void myNotifyDataSetChanged(int position, SmsMessage msg) {
        try {
            MessageFragment f = mFragments.get(position);
            if (f == null) {
                f = getItem(position);
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
}
