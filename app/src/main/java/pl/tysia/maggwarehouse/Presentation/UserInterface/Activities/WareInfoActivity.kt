package pl.tysia.maggwarehouse.Presentation.UserInterface.Activities

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_ware_info.*
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User.Companion.getLoggedUser
import pl.tysia.maggwarehouse.BusinessLogic.Domain.Ware
import pl.tysia.maggwarehouse.Persistance.WareServiceImpl
import pl.tysia.maggwarehouse.R
import java.io.IOException

class WareInfoActivity : AppCompatActivity() {
    private lateinit var ware : Ware

    companion object{
        const val WARE_EXTRA = "pl.tysia.maggwarehouse.ware_extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ware_info)

        ware  = intent.getSerializableExtra(WARE_EXTRA) as Ware

        name_tv.text = ware.name
        location_tv.text = ware.location
        index_tv.text = ware.index
        price_tv.text = ware.price.toString()

        val stringBuilder = StringBuilder()

        for (availability in ware.availabilities!!){
            val textView = TextView(this)

            stringBuilder.append("Magazyn: ")
            stringBuilder.append(availability.warehouse)
            stringBuilder.append("Ilość: ")
            stringBuilder.append(availability.count)

            textView.text = stringBuilder.toString()
            textView.textSize = 14f

            availability_ll.addView(textView)

            val param = textView.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,8,8,8)
            textView.layoutParams = param

            stringBuilder.clear()

        }
    }

    fun onBackClicked(view: View){
        finish()
    }

    fun onAddPhotoClicked(view : View){
        var returnIntent = Intent(this, WareEditorActivity::class.java)
        returnIntent?.putExtra(WARE_EXTRA, ware)
        startActivity(returnIntent);

    }


}
