package fr.castorflex.android.quickanswer.libs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.pojos.QuickAnswer;
import fr.castorflex.android.quickanswer.providers.SettingsProvider;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 16/09/12
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class OverflowLayout extends LinearLayout implements AdapterView.OnItemClickListener {


    public final static int TYPE_EDIT = 0;
    public final static int TYPE_SEND = 1;

    private Animation mAnimationOpen;
    private Animation mAnimationClose;

    private boolean mIsOpened;
    private boolean mIsAnimating;
    private Context mContext;

    private ListView mListView;
    private QuickAnswersAdapter mAdapter;

    private OnItemSelectedListener mListener;




    public interface OnItemSelectedListener {
        public void onItemClick(QuickAnswer qa, int TYPE);
    }


    public OverflowLayout(Context context) {
        super(context);
        init(context);
    }

    public OverflowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OverflowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public boolean onBackPressed() {
        if(mIsOpened){
            close();
            return true;
        }else
            return false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIsOpened = false;
        mIsAnimating = false;
        setVisibility(View.GONE);

        mListView = (ListView) findViewById(R.id.listView_overflow);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
    }

    private void init(Context context) {
        mContext = context;
        mAdapter = new QuickAnswersAdapter();

        //animations
        mAnimationOpen = AnimationUtils.loadAnimation(context, R.anim.anim_overflow_open);
        mAnimationClose = AnimationUtils.loadAnimation(context, R.anim.anim_overflow_close);
        Animation.AnimationListener l = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };
        mAnimationOpen.setAnimationListener(l);
        mAnimationClose.setAnimationListener(l);

    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }

    private void notifyClick(QuickAnswer qa, int type) {
        if (mListener != null)
            mListener.onItemClick(qa, type);
        close();
    }

    public void toggle() {
        if (mIsOpened) {
            close();
        } else {
            open();
        }

    }

    public void close() {
        if (!mIsAnimating && mIsOpened) {
            this.startAnimation(mAnimationClose);
            setVisibility(View.GONE);
            mIsOpened = false;
        }
    }

    public void open() {
        if (!mIsAnimating && !mIsOpened) {
            setVisibility(View.VISIBLE);
            this.startAnimation(mAnimationOpen);
            mIsOpened = true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        QuickAnswer qa = mAdapter.getItem(position);
        notifyClick(qa, TYPE_EDIT);
    }

    public void updateAdapter(){
        mAdapter.updateItems();
    }

    class QuickAnswersAdapter extends BaseAdapter {

        private List<QuickAnswer> mData;
        private LayoutInflater mInflater;

        public QuickAnswersAdapter() {
            mData = SettingsProvider.getQuickAnswers(mContext);
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void updateItems(){
            mData = SettingsProvider.getQuickAnswers(mContext);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public QuickAnswer getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View vi = convertView;
            ViewHolder holder;

            if (vi == null) {
                vi = mInflater.inflate(R.layout.quickanswer_item, null);
                holder = new ViewHolder();
                holder.message = (TextView) vi.findViewById(R.id.textView_message);
                holder.button = (ImageView) vi.findViewById(R.id.imageView_send);
                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            final QuickAnswer item = getItem(position);
            holder.message.setText(item.getMessage());
            holder.button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    notifyClick(item, TYPE_SEND);
                }
            });

            return vi;
        }

        class ViewHolder {
            public TextView message;
            public ImageView button;

        }
    }
}
