package pl.tysia.maggwarehouse.Presentation.UserInterface.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_password_change.*
import kotlinx.android.synthetic.main.app_bar.*
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.Persistance.LoginServiceImpl
import pl.tysia.maggwarehouse.Persistance.LoginServiceMock
import pl.tysia.maggwarehouse.R
import java.io.IOException

class PasswordChangeActivity : AppCompatActivity() {
    private var mAuthTask : ChangePasswordTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_change)

        setSupportActionBar(themedToolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    fun saveChanges(view : View){
        attemptChangePassword()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return true

    }

    private fun attemptChangePassword() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        first_password.error = null
        second_password.error = null

        // Store values at the time of the login attempt.
        val firstPasswordStr = first_password.text.toString()
        val secondPasswordStr = second_password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(firstPasswordStr) && !isPasswordValid(firstPasswordStr)) {
            first_password.error = getString(R.string.error_invalid_password)
            focusView = first_password
            cancel = true
        }else if (!TextUtils.isEmpty(secondPasswordStr) && !isPasswordValid(secondPasswordStr)) {
            second_password.error = getString(R.string.error_invalid_password)
            focusView = second_password
            cancel = true
        }else if (secondPasswordStr != firstPasswordStr) {
            second_password.error = getString(R.string.error_passwords_not_matching)
            focusView = second_password
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
            mAuthTask = ChangePasswordTask(firstPasswordStr)
            mAuthTask!!.execute()
        }
    }


    private fun isPasswordValid(password: String): Boolean {
        return true
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            password_change_form.visibility = if (show) View.GONE else View.VISIBLE
            password_change_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        password_change_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

            password_change_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
            password_change_progress_bar.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        password_change_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            password_change_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
            password_change_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    inner class ChangePasswordTask internal constructor(private val mPassword: String) :
        AsyncTask<String, String, User>() {

        private var exceptionOccured = false


        override fun doInBackground(vararg params: String): User? {

            val loginService = LoginServiceImpl(this@PasswordChangeActivity)
            val user = User.getLoggedUser(applicationContext)

            if (user == null){
                val intent = Intent(this@PasswordChangeActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return null
            }

            return try {
                if (loginService.changePassword(user, mPassword))
                    user else
                    null
            }catch (ex : IOException){
                exceptionOccured = true
                null
            }


        }

        override fun onPostExecute(result: User?) {
            mAuthTask = null
            showProgress(false)

            when {
                result != null -> {
                    finish()
                    Toast.makeText(this@PasswordChangeActivity, getString(R.string.password_changed_toast), Toast.LENGTH_LONG).show()
                }
                exceptionOccured -> okDialog(getString(R.string.connection_error), getString(R.string.connectoin_error_long_message))
                else -> {
                    password.error = getString(R.string.error_incorrect_password)
                    password.requestFocus()
                }
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

    private fun okDialog(title : String, message : String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, whichButton ->
            //get what you need here!
        })
        val b = dialogBuilder.create()
        b.show()
    }
}
