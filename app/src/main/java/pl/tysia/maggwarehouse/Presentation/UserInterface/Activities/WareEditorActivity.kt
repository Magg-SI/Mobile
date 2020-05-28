package pl.tysia.maggwarehouse.Presentation.UserInterface.Activities

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_ware_editor.*
import pl.tysia.maggwarehouse.R
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.BusinessLogic.Domain.Ware
import pl.tysia.maggwarehouse.Persistance.WareServiceImpl
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.EditFoto
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import pl.tysia.maggwarehouse.rotateBitmap


class WareEditorActivity : AppCompatActivity() {
    companion object{
        private const val MY_CAMERA_REQUEST_CODE = 100
      //  private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1234
        const val REQUEST_TAKE_PHOTO = 1
    }

    var currentPhotoPath: String? = null
    private var sendWareTask : SendWareTask? = null
    private var getPhotoTask : GetPhotoTask? = null

    private lateinit var ware : Ware

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_ware_editor)

        ware = intent.getSerializableExtra("scanner_result") as Ware
        product_name.text = ware.name
        product_index.text = ware.index



        //if (ware.photoPath != null) setProductImageBitmap(ware.photoPath)
        if (ware.hasPhoto && ware.id != null) {
            imageProgressBar.visibility = View.VISIBLE
            getPhotoTask = GetPhotoTask(ware.id!!)
            getPhotoTask?.execute()
        }else imageProgressBar.visibility = View.GONE


        val toolbar = findViewById<Toolbar>(R.id.themedToolbar)
        setSupportActionBar(toolbar)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        setZoom(scale_button);

    }

    fun setCrop(view : View){
        product_image.setMode(EditFoto.MODE_CROP)
        crop_button.isActivated = true
        scale_button.isActivated = false
    }

    fun setZoom(view : View){
        product_image.setMode(EditFoto.MODE_SCALE)
        crop_button.isActivated = false
        scale_button.isActivated = true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return true

    }

    private fun setProductImageBitmap(path : String?){
        val file = File(path)
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))

        val rotatedBitmap = rotateBitmap(bitmap)

        product_image.bitmap = rotatedBitmap
        // product_image.setImageBitmap(rotatedBitmap)

    }

    fun takePicture(view : View){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED){
            val arr = arrayOf(Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, arr , 6)

        }else{
            dispatchTakePictureIntent()
            //cameraTest()

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "png_${timeStamp}_", /* prefix */
            ".png", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    private fun dispatchTakePictureIntent() {
        getPhotoTask?.cancel(true)

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "pl.tysia.maggwarehouse.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setProductImageBitmap(currentPhotoPath)
        }
    }



    fun attemptSave(view : View) {
        if (sendWareTask != null) {
            return
        }

        // Store values at the time of the login attempt.
        var bitmap : Bitmap? = null
        if (product_image.bitmap != null)
            bitmap  = product_image.bitmap

        var cancel = false
        val focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (bitmap == null){
            Toast.makeText(this, getString(R.string.picture_required_order), Toast.LENGTH_LONG).show()
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

            ware.photoPath = currentPhotoPath
            ware.imageBitmap = bitmap
            sendWareTask = SendWareTask(ware)
            sendWareTask!!.execute()
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            ware_edit_form.visibility = if (show) View.GONE else View.VISIBLE
            ware_edit_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        ware_edit_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

            ware_editor_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
            ware_editor_progress_bar.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        ware_editor_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            ware_editor_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
            ware_edit_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    private fun okDialog(title : String, message : String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("OK") { _, _ ->

        }
        val b = dialogBuilder.create()
        b.show()
    }

    inner class SendWareTask internal constructor(private val ware: Ware) :
        AsyncTask<String, String, Boolean>() {
        private var exceptionOccured = false
        private val wareService = WareServiceImpl(applicationContext)

        override fun doInBackground(vararg params: String): Boolean? {

            val user = User.getLoggedUser(this@WareEditorActivity)

            return try {
                wareService.sendPhoto(ware, user!!.token!!)
            }catch (e : IOException){
                exceptionOccured = true
                false
            }


        }

        override fun onPostExecute(result: Boolean?) {
            sendWareTask = null
            showProgress(false)

            when {
                result == true -> {
                    ware.photoPath = null

                    finish()
                    Toast.makeText(this@WareEditorActivity, getString(R.string.product_added_toast), Toast.LENGTH_LONG).show()
                }
                exceptionOccured -> okDialog(getString(R.string.connection_error), getString(R.string.connectoin_error_long_message))
                else -> Toast.makeText(this@WareEditorActivity, getString(R.string.product_couldnt_be_found_toast_with_reason, wareService.lastError), Toast.LENGTH_LONG).show()
            }
        }

        override fun onCancelled() {
            sendWareTask = null
            showProgress(false)
        }
    }

    inner class GetPhotoTask internal constructor(private val id: Int) :
        AsyncTask<String, String, Bitmap>() {

        override fun doInBackground(vararg params: String): Bitmap? {
            val user = User.getLoggedUser(this@WareEditorActivity)

            return try {
                val wareService = WareServiceImpl(applicationContext)
                wareService.getPhoto(id, user!!.token!!)
            }catch (e : IOException){
                e.printStackTrace()
                null
            }

        }

        override fun onPostExecute(result: Bitmap?) {
            sendWareTask = null
            showProgress(false)

            imageProgressBar.visibility = View.GONE

            if (result != null) {
                product_image.bitmap = result
                //product_image.setImageBitmap(result)
            }
        }

        override fun onCancelled() {
            sendWareTask = null
            imageProgressBar.visibility = View.GONE
        }
    }
}
