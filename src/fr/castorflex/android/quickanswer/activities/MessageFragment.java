package fr.castorflex.android.quickanswer.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.pojos.Contact;
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

    private String mIdSender;
    private ListView mListView;
    private MessagesAdapter mAdapter;
    private List<SmsMessage> mInitData;
    private LinearLayout mActionbar;
    private Contact mContact;

    public MessageFragment(String sender, List<SmsMessage> data) {
        mInitData = data;
        mIdSender = sender;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);

        mActionbar = (LinearLayout) view.findViewById(R.id.actionbar);


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
            if(mContact.getPhoto() != null)
            ((ImageView)mActionbar.findViewById(R.id.imageView_actionbar)).setImageURI(Uri.parse(mContact.getPhoto()));
            ((TextView) mActionbar.findViewById(R.id.textView_actionbar_big)).setText(mContact.getName());
            ((TextView) mActionbar.findViewById(R.id.textView_actionbar_small)).setText(mIdSender);
        } else {
            ((TextView) mActionbar.findViewById(R.id.textView_actionbar_big)).setText(mIdSender);
            mActionbar.findViewById(R.id.textView_actionbar_small).setVisibility(View.GONE);
        }
    }




    private void initAdapter() {
        List<SmsMessage> list = new ArrayList<SmsMessage>();
        mAdapter = new MessagesAdapter(getActivity(), mInitData);
    }

    public void addSms(SmsMessage msg) {
        mAdapter.addMessage(msg);
        mListView.smoothScrollToPosition(mAdapter.getCount());
    }

    public void notifyChanged() {
        mAdapter.notifyDataSetChanged();
        mListView.smoothScrollToPosition(mAdapter.getCount());
    }
}
