package fr.castorflex.android.quickanswer.activities;

import android.content.Context;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.castorflex.android.quickanswer.R;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 24/08/12
 * Time: 23:27
 * To change this template use File | Settings | File Templates.
 */
public class MessagesAdapter extends BaseAdapter {

    private List<SmsMessage> mData;
    private LayoutInflater mInflater;

    public MessagesAdapter(Context c, List<SmsMessage> data) {
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SmsMessage getItem(int position) {
        return mData.get(position);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getItemId(int position) {
        return position;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addMessage(SmsMessage msg) {
        mData.add(msg);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View vi = convertView;
        ViewHolder holder;
        if (vi == null) {
            vi = mInflater.inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.date = (TextView) vi.findViewById(R.id.textview_message_item_date);
            holder.message = (TextView) vi.findViewById(R.id.textview_message_item_message);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        SmsMessage item = getItem(position);
        holder.message.setText(item.getDisplayMessageBody());

        return vi;
    }

    static class ViewHolder {
        TextView date;
        TextView message;
    }
}
