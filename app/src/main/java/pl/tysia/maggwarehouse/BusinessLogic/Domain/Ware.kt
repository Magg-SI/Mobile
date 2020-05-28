package pl.tysia.maggwarehouse.BusinessLogic.Domain

import android.graphics.Bitmap
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.ICatalogable
import pl.tysia.maggwarehouse.R
import java.io.Serializable

class Ware(var name: String) : ICatalogable, Serializable {
    private var quantityOrdered = 0
    private var quantityTaken = 0

    var id : Int? = null
    var photoPath : String? = null
    var imageBitmap : Bitmap? = null;
    var qrCode : String? = null
    var index : String? = "index"

    var hasPhoto = false


    constructor(qrCode : String, id : Int, index : String, name: String, hasPhoto : Boolean ) : this(name){
        this.qrCode = qrCode
        this.id = id
        this.index = index

        this.hasPhoto = hasPhoto
    }

    override fun getTitle(): String {
        return name
    }

    override fun getShortDescription(): String {
        return "Short description"
    }

    override fun getImageResource(): Int {
        return R.drawable.picture
    }

    override fun getQuantityOrdered(): Int {
        return quantityOrdered
    }

    override fun setQuantityOrdered(quantityOrdered: Int) {
        this.quantityOrdered = quantityOrdered
    }

    override fun getQuantityTaken(): Int {
        return quantityTaken
    }

    override fun setQuantityTaken(quantityTaken: Int) {
        this.quantityTaken = quantityTaken
    }
}