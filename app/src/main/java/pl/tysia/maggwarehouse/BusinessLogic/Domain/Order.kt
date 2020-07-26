package pl.tysia.maggwarehouse.BusinessLogic.Domain

import android.graphics.Bitmap
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.ICatalogable
import java.io.Serializable

data class Order(var id: Int, var warehouse: String, var documentDate: String, var documentNr: String, var comments: String)
    : ICatalogable, Serializable{

    override fun getTitle() = "Zamówienie nr $documentNr \nz dnia $documentDate"

    override fun getShortDescription(): String {
        var res =  "Magazyn: $warehouse\n"
        if (comments != "null") res += "\nUwagi: "+ comments

        return res
    }

    override fun isMarked(): Boolean {
        return false
    }
}