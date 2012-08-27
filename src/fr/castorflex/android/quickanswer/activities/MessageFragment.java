package fr.castorflex.android.quickanswer.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.libs.SmsSenderThread;
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
public class MessageFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private String mIdSender;
    private ListView mListView;
    private MessagesAdapter mAdapter;
    private List<Message> mInitData;
    private LinearLayout mActionbar;
    private Contact mContact;
    private ImageButton mSendButton;
    private EditText mEditTextMessage;

    public MessageFragment(String sender, List<Message> data) {
        mInitData = data;
        mIdSender = sender;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);

        mActionbar = (LinearLayout) view.findViewById(R.id.actionbar);

        mSendButton = (ImageButton) view.findViewById(R.id.imageButton_send);
        mEditTextMessage = (EditText) view.findViewById(R.id.editText_message);

        mListView = (ListView) view.findViewById(R.id.listview_messages);
        View v = new View(getActivity());
        v.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MeasuresUtils.DpToPx(44)));
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        mListView.setStackFromBottom(true);


        initViews();
        initAdapter();
        mListView.setAdapter(mAdapter);


        return view;
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

        mEditTextMessage.addTextChangedListener(this);
        mSendButton.setClickable(false);
        mSendButton.setEnabled(false);
        mSendButton.setOnClickListener(this);
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
        mAdapter.notifyDataSetChanged();
        mListView.smoothScrollToPosition(mAdapter.getCount());
    }


    //////////////////////IMPLEMENTS/////////////////////////////////////////////////
    @Override
    public void onClick(View view) {
        if (view == mSendButton) {
            hideKeyboard();

            String messageBody = mEditTextMessage.getText().toString();
            mEditTextMessage.setText("");
            new SmsSenderThread(mIdSender, messageBody).start();
            ((PopupActivity) getActivity()).removeFragment(mIdSender);
        }
    }

    public void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSendButton.getWindowToken(), 0);
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
}
