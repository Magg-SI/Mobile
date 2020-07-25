package pl.tysia.maggwarehouse.BusinessLogic.Domain

import android.graphics.Bitmap
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.ICatalogable
import java.io.Serializable

data class Order(var id: Int, var warehouse: String, var documentDate: String, var documentNr: String, var comments: String)
    : ICatalogable, Serializable{

    override fun getTitle() = "Zamówienie nr $documentNr z dnia $documentDate"

    override fun getShortDescription(): String {
        var res =  "Magazyn: $warehouse"
        if (comments != "null") res += comments

        return res
    }

    override fun getImage(): Bitmap? {
        return null
    }
}