package pl.tysia.maggwarehouse.BusinessLogic.Domain

import android.graphics.Bitmap
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.ICatalogable
import pl.tysia.maggwarehouse.R
import java.io.Serializable

data class Ware(var name: String) : ICatalogable, Serializable {
    private var quantityOrdered = 0
    private var quantityTaken = 0

    var id : Int? = null
    var photoPath : String? = null
    var imageBitmap : Bitmap? = null;
    var qrCode : String? = null
    var index : String? = "index"
    var location : String? = null
    var price : Double? = null
    var availabilities : List<Availability>? = null

    var hasPhoto = false


    constructor(qrCode : String, id : Int, index : String, name: String, hasPhoto : Boolean,  location : String, price : Double) : this(name){
        this.qrCode = qrCode
        this.id = id
        this.index = index
        this.location = location
        this.price = price

        this.hasPhoto = hasPhoto
    }

    override fun getTitle(): String {
        return name
    }

    override fun isMarked(): Boolean {
        return false
    }

    override fun getShortDescription(): String {
        return "Short description"
    }

}