package pl.tysia.maggwarehouse.Presentation.UserInterface.Activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import pl.tysia.maggwarehouse.R
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.BusinessLogic.NetAddressManager
import pl.tysia.maggwarehouse.Presentation.UserInterface.Fragments.DialogFragmentPassword




class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val MY_CAMERA_REQUEST_CODE = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        PreferenceManager.setDefaultValues(this, R.xml.pref_address, false)
/*
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)*/

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this@MainActivity, ScannerActivity::class.java)
                startActivity(intent)
            } else {
            }
        }
    }


    fun onScanClicked(view : View){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED){
            val arr = arrayOf(Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, arr , MY_CAMERA_REQUEST_CODE)

        }else{
            val intent = Intent(this@MainActivity, ScannerActivity::class.java)
            startActivity(intent)

        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        val user = User.getLoggedUser(applicationContext)

        if (name_tv!= null)
            name_tv.text = user?.login
        //locker_tv.text = getString(R.string.nav_bar_locker_nr, user?.locker)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.nav_logout -> {
                logout()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun logout(){
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage(getString(R.string.confirm_logout)).
            setPositiveButton("Tak", DialogInterface.OnClickListener { dialog, whichButton ->
                User.logout(applicationContext)
                dialog.dismiss()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
        })
            .setNegativeButton("Nie", DialogInterface.OnClickListener { dialog, whichButton ->
            dialog.dismiss()
        })
            .show()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_catalog -> {
            }

            R.id.nav_logout -> {
                logout()
            }

            R.id.nav_change_password -> {
                val fragmentManager = supportFragmentManager
                DialogFragmentPassword.newInstance(getString(R.string.password_confirmation_order)).show(fragmentManager, "TAG_DOC_CHOOSE_FRAGMENT")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
