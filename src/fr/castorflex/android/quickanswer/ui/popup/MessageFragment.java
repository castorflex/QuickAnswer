package fr.castorflex.android.quickanswer.ui.popup;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.*;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.pojos.Contact;
import fr.castorflex.android.quickanswer.pojos.Message;
import fr.castorflex.android.quickanswer.providers.ContactProvider;
import fr.castorflex.android.quickanswer.utils.MeasuresUtils;

import java.io.InputStream;
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

    private static final String ARG_LIST = "arg_list";
    private static final String ARG_SENDER = "arg_sender";
    private static final String ARG_LEFT = "arg_left";
    private static final String ARG_RIGHT = "arg_right";

    private static final int ANIM_DURATION = 700;

    private String mIdSender;
    private ListView mListView;
    private MessagesAdapter mAdapter;
    private ArrayList<Message> mInitData;
    private LinearLayout mActionbar;
    private Contact mContact;


    private View mLeftIndicator;
    private View mRightIndicator;
    private boolean mIsLeftIndicActivated;
    private boolean mIsRightIndicActivated;

    protected AlphaAnimation mFadeAnimation = new AlphaAnimation(0.0f, 1.0f);


    public static MessageFragment newInstance(String sender, ArrayList<Message> data,
                                              boolean leftIndic, boolean rightIndic) {
        MessageFragment instance = new MessageFragment();
        Bundle b = new Bundle();
        b.putBoolean(ARG_LEFT, leftIndic);
        b.putBoolean(ARG_RIGHT, rightIndic);
        b.putString(ARG_SENDER, sender);
        b.putParcelableArrayList(ARG_LIST, data);

        instance.setArguments(b);
        return instance;
    }


    public MessageFragment() {
        super();
    }


    @Override
    public void onSaveInstanceState(Bundle b) {
        b.putBoolean(ARG_LEFT, mIsLeftIndicActivated);
        b.putBoolean(ARG_RIGHT, mIsRightIndicActivated);
        b.putString(ARG_SENDER, mIdSender);
        b.putParcelableArrayList(ARG_LIST, mInitData);
        super.onSaveInstanceState(b);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        mInitData = getArguments().getParcelableArrayList(ARG_LIST);
        mIdSender = getArguments().getString(ARG_SENDER);
        mIsLeftIndicActivated = getArguments().getBoolean(ARG_LEFT);
        mIsRightIndicActivated = getArguments().getBoolean(ARG_RIGHT);

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
        header.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_selector));
        AbsListView.LayoutParams p = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                MeasuresUtils.DpToPx(44));
        header.setLayoutParams(p);
        header.setFocusable(false);
        header.setClickable(false);
        header.setFocusableInTouchMode(false);
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
        mContact = ContactProvider.getInstance().getContact(mIdSender, getActivity());
        QuickContactBadge badge = (QuickContactBadge) mActionbar.findViewById(R.id.imageView_actionbar);
        if (mContact == null)
            mContact = new Contact(getString(R.string.unknown), mIdSender, null);

        badge.assignContactFromPhone(mContact.getNumber(), false);
        setImagePhoto(badge);

        ((TextView) mActionbar.findViewById(R.id.textView_actionbar_big)).setText(mContact.getName());
        ((TextView) mActionbar.findViewById(R.id.textView_actionbar_small)).setText(mIdSender);


    }

    private void setImagePhoto(QuickContactBadge badge) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

            if (mContact.getPhoto() != null) {
                InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
                        getActivity().getContentResolver(), Uri.parse(mContact.getPhoto()));
                if (input != null) {
                    badge.setImageBitmap(BitmapFactory.decodeStream(input));
                }
            }
            if (badge.getDrawable() == null)
                badge.setVisibility(View.GONE);

        } else {
            if (mContact.getPhoto() != null)
                badge.setImageURI(Uri.parse(mContact.getPhoto()));
            if (badge.getDrawable() == null)
                badge.setImageToDefault();
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
