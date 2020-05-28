package pl.tysia.maggwarehouse.Presentation.UserInterface.Activities

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.view.MenuItem
import pl.tysia.maggwarehouse.R
import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()

        fragmentManager.beginTransaction()
            .replace(android.R.id.content, AddressPreferenceFragment())
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
       // loadHeadersFromResource(R.xml.pref_headers, target)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || AddressPreferenceFragment::class.java.name == fragmentName
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class AddressPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_address)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.


            val privateNetUsage : SwitchPreference = findPreference("private_network_usage") as SwitchPreference
            val privateNetPref : EditTextPreference = findPreference("target_private_net_address") as EditTextPreference

          //  if (privateNetPref.text == null || privateNetPref.text.isEmpty()) privateNetUsage.isEnabled = false

            bindPreferenceSummaryToValue(privateNetUsage)
            bindPreferenceSummaryToValue(findPreference("target_public_net_address"))
            bindPreferenceSummaryToValue(privateNetPref)
            bindPreferenceSummaryToValue(findPreference("picture_size"))

            val prefChangeListener = Preference.OnPreferenceChangeListener{ _, newVal ->
                privateNetUsage.isEnabled = newVal != null && (newVal as String).isNotEmpty()
                true
            }

            privateNetPref.onPreferenceChangeListener = prefChangeListener

        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }


    companion object {
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is SwitchPreference) {
                preference.summary = if (value as Boolean) "Korzystanie z sieci prywatnej" else "Korzystanie z sieci firmowej"

            }else if (preference is ListPreference){
                val index = preference.findIndexOfValue(stringValue)
                preference.summary = preference.entryValues[index]
            }
            else{
                preference.summary = stringValue
            }
            true
        }


        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            if (preference is SwitchPreference)
                sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getBoolean(preference.key, false)
                )
            else
                sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, "")
                )
        }
    }
}
