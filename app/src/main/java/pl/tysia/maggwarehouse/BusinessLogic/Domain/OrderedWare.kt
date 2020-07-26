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

    var marked = false

    override fun isMarked(): Boolean {
        return marked || orderedNumber <= (packedNumber + postponedNumber + cancelledNumber)
    }

    override fun getShortDescription(): String {
        return "Nazwa towaru: $productName\n" +
                "\nLiczba zamówionych: $orderedNumber"
    }

    override fun getTitle(): String {
        return "$index\n$location"
    }

}