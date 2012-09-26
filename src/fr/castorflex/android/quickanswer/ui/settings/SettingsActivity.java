package fr.castorflex.android.quickanswer.ui.settings;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.RingtonePreference;
import android.text.util.Linkify;
import android.view.ContextThemeWrapper;
import android.widget.TextView;
import fr.castorflex.android.quickanswer.R;
import fr.castorflex.android.quickanswer.providers.SettingsProvider;
import fr.castorflex.android.quickanswer.utils.MeasuresUtils;

public class SettingsActivity extends com.actionbarsherlock.app.SherlockPreferenceActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //Recuperation des preferences
        Preference prefQA = findPreference(getString(R.string.pref_key_qa_manage));
        Preference prefVersion = findPreference(getString(R.string.pref_key_version));
        Preference prefAbout = findPreference(getString(R.string.pref_key_about));
        Preference prefRate = findPreference(getString(R.string.pref_key_rate));

        prefVersion.setSummary(SettingsProvider.getApplicationVersion(this));

        //Init des Listeners
        prefQA.setOnPreferenceClickListener(new OnQuickAnswerPreferenceClickListener());
        prefAbout.setOnPreferenceClickListener(new OnAboutPreferenceClickListener());
        prefRate.setOnPreferenceClickListener(new OnRatePreferenceClickListener());
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.QAPopup));

        final TextView tv = new TextView(this);
        tv.setPadding(MeasuresUtils.DpToPx(4),
                MeasuresUtils.DpToPx(4),
                MeasuresUtils.DpToPx(4),
                MeasuresUtils.DpToPx(4));
        tv.setText(getText(R.string.about_text));
        Linkify.addLinks(tv, Linkify.ALL);

        builder.setTitle(R.string.pref_about_title)
                .setView(tv)
                .setIcon(R.drawable.ic_launcher)
                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create().show();

    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
        }
    }

    class OnAboutPreferenceClickListener implements Preference.OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            SettingsActivity.this.showAboutDialog();
            return true;
        }
    }

    class OnRatePreferenceClickListener implements Preference.OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            launchMarket();
            return true;
        }
    }

    class OnQuickAnswerPreferenceClickListener implements Preference.OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(SettingsActivity.this, QuickAnswersActivity.class);
            SettingsActivity.this.startActivity(intent);
            return true;
        }
    }

}
