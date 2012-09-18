package fr.castorflex.android.quickanswer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.view.Window;
import fr.castorflex.android.quickanswer.libs.actionbar.ActionBarPreferenceActivity;
import fr.castorflex.android.quickanswer.providers.SettingsProvider;
import fr.castorflex.android.quickanswer.ui.QuickAnswersActivity;

public class SettingsActivity extends ActionBarPreferenceActivity {

    private CheckBoxPreference mPrefActivated;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); // add this line
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //Recuperation des preferences
        mPrefActivated = (CheckBoxPreference) findPreference("qa_activate");
        Preference prefQA = findPreference("manage_qa");
        Preference prefVersion = findPreference("version");
        Preference prefAbout = findPreference("about");
        Preference prefRate = findPreference("rate");

        //init des vues
        boolean enabled = SettingsProvider.isAppEnabled(this);
        mPrefActivated.setChecked(enabled);
        mPrefActivated.setSummary(enabled ?
                R.string.pref_general_qa_activation_summary_false :
                R.string.pref_general_qa_activation_summary_true);
        prefVersion.setSummary(SettingsProvider.getApplicationVersion(this));

        //Init des Listeners
        mPrefActivated.setOnPreferenceChangeListener(new OnActivationChangeListener());
        prefQA.setOnPreferenceClickListener(new OnQuickAnswerPreferenceClickListener());
        prefAbout.setOnPreferenceClickListener(new OnAboutPreferenceClickListener());
        prefRate.setOnPreferenceClickListener(new OnRatePreferenceClickListener());


    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
        }
    }

    class OnActivationChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            SettingsProvider.setAppEnabled(SettingsActivity.this, (Boolean)o);
            mPrefActivated.setSummary((Boolean) o ?
                    R.string.pref_general_qa_activation_summary_false :
                    R.string.pref_general_qa_activation_summary_true);
            return true;
        }
    }

    class OnAboutPreferenceClickListener implements Preference.OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference preference) {
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
