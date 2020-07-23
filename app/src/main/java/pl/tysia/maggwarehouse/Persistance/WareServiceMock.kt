package pl.tysia.maggwarehouse.Persistance

import android.graphics.Bitmap
import pl.tysia.maggwarehouse.BusinessLogic.Domain.Ware

class WareServiceMock : WareService {
    override fun sendPhoto(ware: Ware, token: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPhoto(id: Int, token: String): Bitmap? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendWare(wareCode: String, locationCode: String, token: String): Boolean {
        return true
    }

    override fun testShelf(locationCode: String, token: String): Boolean {
        return true
    }

    override fun getWare(qrCode: String, token: String): Ware {
        return Ware("przykładowy towar")
    }


    companion object {
        val wares : ArrayList<Ware> = ArrayList()

        init {
            wares.add(Ware("test0"))
        }
    }
}