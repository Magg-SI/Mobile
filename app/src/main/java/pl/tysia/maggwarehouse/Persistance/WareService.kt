package pl.tysia.maggwarehouse.Persistance

import android.graphics.Bitmap
import pl.tysia.maggwarehouse.BusinessLogic.Domain.Ware
import java.io.IOException

interface WareService {
    @Throws (IOException::class)
    fun getWare(qrCode : String, token : String) : Ware?
    @Throws (IOException::class)
    fun sendPhoto(ware: Ware, token : String): Boolean
    @Throws (IOException::class)
    fun getPhoto(id : Int, token : String) : Bitmap?
}