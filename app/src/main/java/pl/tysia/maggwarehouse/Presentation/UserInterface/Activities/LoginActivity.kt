package pl.tysia.maggwarehouse.Presentation.UserInterface.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList
import android.content.Intent
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.nav_header_main.*
import pl.tysia.maggwarehouse.R
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.BusinessLogic.NetAddressManager
import pl.tysia.maggwarehouse.Persistance.LoginService
import pl.tysia.maggwarehouse.Persistance.LoginServiceImpl

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var mAuthTask: UserLoginTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NetAddressManager.setDefaultAddressesIfEmpty(applicationContext)

        if (User.getLoggedUser(applicationContext) != null) openMainActivity()


        setContentView(R.layout.activity_login)
        // Set up the login form.
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { attemptLogin() }

        val toolbar = findViewById<Toolbar>(R.id.themedToolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_settings -> {
                val intent = Intent(this@LoginActivity, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return true

    }

    private fun openMainActivity(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            mAuthTask = UserLoginTask(emailStr, passwordStr)
            mAuthTask!!.execute()
        }
    }

    private fun isEmailValid(login: String): Boolean {
        //TODO: Replace this with your own logic
        return true
    }

    private fun isPasswordValid(password: String): Boolean {
        return true
    }

    private fun isLockerNumberValid(number: String): Boolean {
        return true
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            email_login_form.visibility = if (show) View.GONE else View.VISIBLE
            email_sign_in_button.visibility = if (show) View.GONE else View.VISIBLE
            email_login_form.animate()
            email_sign_in_button.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        email_login_form.visibility = if (show) View.GONE else View.VISIBLE
                        email_sign_in_button.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            email_login_form.visibility = if (show) View.GONE else View.VISIBLE
            email_sign_in_button.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.login_menu, menu)

        return true

    }


    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(
            this,
            // Retrieve data rows for the device user's 'profile' contact.
            Uri.withAppendedPath(
                ContactsContract.Profile.CONTENT_URI,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY
            ), ProfileQuery.PROJECTION,

            // Select only email addresses.
            ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(
                ContactsContract.CommonDataKinds.Email
                    .CONTENT_ITEM_TYPE
            ),

            // Show primary email addresses first. Note that there won't be
            // a primary email address if the user hasn't specified one.
            ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
        )
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        val lockers = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            lockers.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(
            this@LoginActivity,
            android.R.layout.simple_dropdown_item_1line, emailAddressCollection
        )

        email.setAdapter(adapter)
    }


    object ProfileQuery {
        val PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.IS_PRIMARY
        )
        val ADDRESS = 0
        val LOCKER = 0
        val IS_PRIMARY = 1
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    inner class UserLoginTask internal constructor(mEmail: String, mPassword: String) :
        AsyncTask<String, String, Int>() {
        private var user : User = User(mEmail, mPassword)


        override fun doInBackground(vararg params: String): Int? {

            val loginService = LoginServiceImpl(applicationContext)

            return loginService.authenticateUser(user)

        }

        override fun onPostExecute(result: Int?) {
            mAuthTask = null
            showProgress(false)

            if (result == LoginService.OK) {
                //user.setLogged(applicationContext)
                openMainActivity()
            } else if (result == LoginService.WRONG_PASSWORD) {
                password.error = getString(R.string.error_incorrect_password)
                password.requestFocus()
            } else if (result == LoginService.WRONG_LOGIN) {
                email.error = getString(R.string.error_no_such_user)
                email.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }


}
