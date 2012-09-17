package fr.castorflex.android.quickanswer;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import fr.castorflex.android.quickanswer.libs.actionbar.ActionBarPreferenceActivity;
import fr.castorflex.android.quickanswer.providers.SettingsProvider;

public class SettingsActivity extends ActionBarPreferenceActivity {


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); // add this line
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //Recuperation des preferences
        CheckBoxPreference prefActivated = (CheckBoxPreference) findPreference("qa_activate");
        Preference prefQA = findPreference("manage_qa");
        Preference prefVersion = findPreference("version");
        Preference prefAbout = findPreference("about");
        Preference prefRate = findPreference("rate");

        //init des vues
        boolean enabled = SettingsProvider.isAppEnabled(this);
        prefActivated.setChecked(enabled);
        prefActivated.setSummary(enabled ?
                R.string.pref_general_qa_activation_summary_true :
                R.string.pref_general_qa_activation_summary_false);
        prefVersion.setSummary(SettingsProvider.getApplicationVersion(this));

        //Init des Listeners
        prefActivated.setOnPreferenceChangeListener(new OnActivationChangeListener());
        prefQA.setOnPreferenceClickListener(new OnQuickAnswerPreferenceClickListener());
        prefAbout.setOnPreferenceClickListener(new OnAboutPreferenceClickListener());
        prefQA.setOnPreferenceClickListener(new OnRatePreferenceClickListener());

    }

    class OnActivationChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
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
            return true;
        }
    }

    class OnQuickAnswerPreferenceClickListener implements Preference.OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            return true;
        }
    }
}
