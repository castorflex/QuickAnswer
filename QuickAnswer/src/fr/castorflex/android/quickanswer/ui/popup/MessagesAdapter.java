package fr.castorflex.android.quickanswer.ui.popup;

import android.content.Context;
import android.content.Intent;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
    private Context mContext;

    public MessagesAdapter(Context c, List<Message> data) {
        mContext = c;
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
    public int getItemViewType(int position) {
        return mData.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View vi = convertView;
        ViewHolder holder;
        final Message item = getItem(position);

        switch (getItemViewType(position)) {
            case Message.TYPE_SMS:

                if (vi == null) {
                    vi = mInflater.inflate(R.layout.message_item_sms, null);
                    holder = new ViewHolder();
                    holder.date = (TextView) vi.findViewById(R.id.textview_message_item_date);
                    holder.message = (TextView) vi.findViewById(R.id.textview_message_item_message);
                    vi.setTag(holder);
                } else {
                    holder = (ViewHolder) vi.getTag();
                }


                holder.message.setText(item.getMessage());

                Linkify.addLinks(holder.message, Linkify.ALL);
                holder.date.setText(mDateFormat.format(item.getDate()));

                break;
            case Message.TYPE_MMS:
                if (vi == null) {
                    vi = mInflater.inflate(R.layout.message_item_mms, null);
                    holder = new ViewHolder();
                    holder.date = (TextView) vi.findViewById(R.id.textview_message_item_date);
                    holder.button = (Button)vi.findViewById(R.id.buttonMMS);
                    vi.setTag(holder);
                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                holder.date.setText(mDateFormat.format(item.getDate()));


                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.putExtra("address", item.getSender());
                        sendIntent.setType("vnd.android-dir/mms-sms");
                        mContext.startActivity(sendIntent);

                        if(mContext instanceof PopupActivity){
                            ((PopupActivity)mContext).removeFragment(item.getSender());
                        }

                    }
                });
                break;
        }

        return vi;
    }

    static class ViewHolder {
        Button button;
        TextView date;
        TextView message;
    }
}
