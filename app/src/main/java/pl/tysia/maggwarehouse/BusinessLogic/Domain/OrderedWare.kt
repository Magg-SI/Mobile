package pl.tysia.maggwarehouse.BusinessLogic.Domain

import android.graphics.Bitmap
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.ICatalogable
import java.io.Serializable

data class OrderedWare(var positionID: Int,
                       var index : String,
                       var productName : String,
                       var location : String,
                       var qrCode : String,
                       var orderedNumber: Double,
                       var packedNumber: Double,
                       var postponedNumber: Double,
                       var cancelledNumber: Double,
                       var availability: Double) : ICatalogable, Serializable {


    override fun getImage(): Bitmap? {
        return null
    }

    override fun getShortDescription(): String {
        return "Nazwa towaru: $productName\n" +
                "Liczba zamówionych: $orderedNumber"
    }

    override fun getTitle(): String {
        return "Lokalizacja: $location\n" +
                "Indeks: $index"
    }

}