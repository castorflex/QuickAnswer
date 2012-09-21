package fr.castorflex.android.quickanswer.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.pojos.QuickAnswer;
import fr.castorflex.android.quickanswer.utils.MeasuresUtils;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 18/09/12
 * Time: 15:33
 * To change this template use File | Settings | File Templates.
 */
public class QuickAnswersActivity extends Activity {

    private ListView mListView;
    private QuickAnswersAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.quickanswers_layout);

        mListView = (ListView) findViewById(R.id.listView_qa);

        mAdapter = new QuickAnswersAdapter(this);
        mListView.setAdapter(mAdapter);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings_qa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new:
                showEditDialog(-1, null);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void showEditDialog(final int position, final QuickAnswer item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(position >= 0 ? R.string.edit : R.string.add);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final EditText et = (EditText) inflater.inflate(R.layout.edittext_holo, null);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(MeasuresUtils.DpToPx(4), MeasuresUtils.DpToPx(15),
                MeasuresUtils.DpToPx(4), MeasuresUtils.DpToPx(15));
        et.setHint(R.string.edittext_hint_qa);
        et.setLayoutParams(p);
        if (item != null) {
            et.setText(item.getMessage());
            et.setSelection(item.getMessage().length());
        }
        et.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(et, 0);
            }
        }, 200);
        builder.setView(et);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setPositiveButton(R.string.valid, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (et.getText() != null && et.getText().length() > 0) {
                    if (item != null) {
                        mAdapter.setMessageEdited(position, et.getText().toString());
                    } else {
                        mAdapter.addNewMessage(et.getText().toString());
                    }
                    dialogInterface.dismiss();
                }
            }
        });

        builder.create().show();
    }
}
