package fr.castorflex.android.quickanswer.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.pojos.QuickAnswer;
import fr.castorflex.android.quickanswer.providers.SettingsProvider;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 18/09/12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class QuickAnswersAdapter extends BaseAdapter {

    private ArrayList<QuickAnswer> mData;
    private QuickAnswersActivity mActivity;
    private LayoutInflater mInflater;

    public QuickAnswersAdapter(QuickAnswersActivity activity) {
        mActivity = activity;
        mData = SettingsProvider.getQuickAnswers(activity);
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public QuickAnswer getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        View vi = convertView;
        ViewHolder holder;
        if (vi == null) {
            vi = mInflater.inflate(R.layout.quickanswer_settings_item, null);
            holder = new ViewHolder();
            holder.delete = (ImageView) vi.findViewById(R.id.imageButton_delete);
            holder.edit = (ImageView) vi.findViewById(R.id.imageButton_edit);
            holder.text = (TextView) vi.findViewById(R.id.textView_qa_text);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        QuickAnswer item = getItem(position);
        holder.text.setText(item.getMessage());

        holder.delete.setOnClickListener(new OnDeleteListener(position));
        holder.edit.setOnClickListener(new OnEditListener(position));

        return vi;
    }

    public void setMessageEdited(int position, String text) {
        mData.get(position).setMessage(text);
        notifyDataSetChanged();
        saveQuickAnswers();
    }

    public void addNewMessage(String text) {
        mData.add(new QuickAnswer(text));
        notifyDataSetChanged();
        saveQuickAnswers();
    }

    public void saveQuickAnswers(){
        SettingsProvider.setQuickAnswers(mActivity, mData);
    }

    static class ViewHolder {
        TextView text;
        ImageView delete;
        ImageView edit;
    }

    class OnEditListener implements View.OnClickListener {
        private int mPosition;

        public OnEditListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            mActivity.showEditDialog(mPosition, getItem(mPosition));
        }
    }

    class OnDeleteListener implements View.OnClickListener {
        private int mPosition;

        public OnDeleteListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            mData.remove(mPosition);
            notifyDataSetChanged();
            saveQuickAnswers();
        }
    }
}
