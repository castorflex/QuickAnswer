package fr.castorflex.android.quickanswer.ui.popup;

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.pojos.Message;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Castorflex
 * Date: 24/08/12
 * Time: 23:27
 * To change this template use File | Settings | File Templates.
 */
public class MessagesAdapter extends BaseAdapter {

    private List<Message> mData;
    private LayoutInflater mInflater;
    private SimpleDateFormat mDateFormat;

    public MessagesAdapter(Context c, List<Message> data) {
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
        mDateFormat = new SimpleDateFormat("HH:mm");
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Message getItem(int position) {
        return mData.get(position);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getItemId(int position) {
        return position;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addMessage(Message msg) {
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

        Message item = getItem(position);

        if (item.getType() == Message.TYPE_MMS)
            holder.message.setText("MMS");
        else
            holder.message.setText(item.getMessage());

        Linkify.addLinks(holder.message, Linkify.ALL);
        holder.date.setText(mDateFormat.format(item.getDate()));

        return vi;
    }

    static class ViewHolder {
        TextView date;
        TextView message;
    }
}
