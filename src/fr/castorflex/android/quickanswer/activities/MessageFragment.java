package fr.castorflex.android.quickanswer.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.*;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.pojos.Contact;
import fr.castorflex.android.quickanswer.pojos.Message;
import fr.castorflex.android.quickanswer.providers.ContactProvider;
import fr.castorflex.android.quickanswer.utils.MeasuresUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 25/08/12
 * Time: 00:38
 * To change this template use File | Settings | File Templates.
 */
public class MessageFragment extends Fragment {

    private static final int ANIM_DURATION = 700;

    private String mIdSender;
    private ListView mListView;
    private MessagesAdapter mAdapter;
    private List<Message> mInitData;
    private LinearLayout mActionbar;
    private Contact mContact;


    private View mLeftIndicator;
    private View mRightIndicator;
    private boolean mIsLeftIndicActivated;
    private boolean mIsRightIndicActivated;

    protected AlphaAnimation mFadeAnimation = new AlphaAnimation(0.0f, 1.0f);


    public MessageFragment(String sender, List<Message> data, boolean leftIndic, boolean rightIndic) {
        mInitData = data;
        mIdSender = sender;
        mIsLeftIndicActivated = leftIndic;
        mIsRightIndicActivated = rightIndic;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);

        mActionbar = (LinearLayout) view.findViewById(R.id.actionbar);


        mLeftIndicator = mActionbar.findViewById(R.id.imageView_prev);
        mRightIndicator = mActionbar.findViewById(R.id.imageView_next);
        initAnimation();
        if (mIsLeftIndicActivated) {
            mLeftIndicator.setVisibility(View.VISIBLE);
            mLeftIndicator.startAnimation(mFadeAnimation);
        }
        if (mIsRightIndicActivated) {
            mRightIndicator.setVisibility(View.VISIBLE);
            mRightIndicator.startAnimation(mFadeAnimation);
        }

        mListView = (ListView) view.findViewById(R.id.listview_messages);
        View v = new View(getActivity());
        v.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MeasuresUtils.DpToPx(44)));
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        mListView.setStackFromBottom(true);

        View header = new View(getActivity());
        AbsListView.LayoutParams p = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                MeasuresUtils.DpToPx(44));
        header.setLayoutParams(p);
        mListView.addHeaderView(header);
        mListView.setHeaderDividersEnabled(false);

        initViews();
        initAdapter();
        mListView.setAdapter(mAdapter);


        return view;
    }

    private void initAnimation() {
        mFadeAnimation.setDuration(ANIM_DURATION);
        mFadeAnimation.setFillAfter(true);

        mFadeAnimation.setInterpolator(new LinearInterpolator());

        mFadeAnimation.setRepeatCount(Animation.INFINITE);
        mFadeAnimation.setRepeatMode(Animation.REVERSE);
    }

    private void initViews() {
        mContact = ContactProvider.getContactName(mIdSender, getActivity());
        if (mContact != null) {
            if (mContact.getPhoto() != null)
                ((ImageView) mActionbar.findViewById(R.id.imageView_actionbar)).setImageURI(Uri.parse(mContact.getPhoto()));
            ((TextView) mActionbar.findViewById(R.id.textView_actionbar_big)).setText(mContact.getName());
            ((TextView) mActionbar.findViewById(R.id.textView_actionbar_small)).setText(mIdSender);
        } else {
            ((TextView) mActionbar.findViewById(R.id.textView_actionbar_big)).setText(mIdSender);
            mActionbar.findViewById(R.id.textView_actionbar_small).setVisibility(View.GONE);
        }


    }


    private void initAdapter() {
        List<Message> list = new ArrayList<Message>();
        mAdapter = new MessagesAdapter(getActivity(), mInitData);
    }

    public void addSms(Message msg) {
        mAdapter.addMessage(msg);
        mListView.smoothScrollToPosition(mAdapter.getCount());
    }

    public void notifyChanged() {
        if (mAdapter != null && mListView != null) {
            mAdapter.notifyDataSetChanged();
            mListView.smoothScrollToPosition(mAdapter.getCount());
        }
    }

    public void notifyNext() {
        mRightIndicator.setVisibility(View.VISIBLE);
    }

    public void notifyPrev() {
        mLeftIndicator.setVisibility(View.VISIBLE);
    }

    public String getSender() {
        return mIdSender;
    }
}
