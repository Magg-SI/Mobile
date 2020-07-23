package pl.tysia.maggwarehouse.Presentation.PresentationLogic

import android.content.Context
import android.support.v7.app.AlertDialog

public fun okDialog(title: String, message: String, context : Context) {
    val builder =
        AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
        .setCancelable(false)
        .setPositiveButton("OK") { dialog, id ->
            //do things
        }
    val alert = builder.create()
    alert.show()
}