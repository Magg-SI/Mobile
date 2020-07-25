package pl.tysia.maggwarehouse.Persistance

import android.content.Context
import android.graphics.Bitmap
import android.preference.PreferenceManager
import android.util.Base64
import org.json.JSONObject
import pl.tysia.maggwarehouse.BusinessLogic.Domain.Ware
import pl.tysia.maggwarehouse.Persistance.Connection.URLConnectionManagerImpl
import pl.tysia.maggwarehouse.resizeBitmap
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import pl.tysia.maggwarehouse.BusinessLogic.Domain.Availability
import java.io.IOException


class WareServiceImpl(private val context : Context ) : WareService {
    private val connectionManager = URLConnectionManagerImpl()
    private val connectionURL = connectionManager.getConnectionURL(context)

    var lastError : String? = null

    @Throws (IOException::class)
    override fun getPhoto(id: Int, token:String): Bitmap? {
        connectionManager.setConnection(connectionURL)
        val res = connectionManager.post(getPhotoGetterJSON(id, token))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)
        return if (resCode == RESPONSE_OK){
            return photoFromString(jsonRes.getString("foto"))

        }else{
            lastError = jsonRes.getString(JSON_ERROR_DESC)
            null

        }
    }


    private fun getPhotoGetterJSON(id: Int, token:String): String{
        val jsonObj = JSONObject()

        jsonObj.put("func", "getFoto")
        jsonObj.put("token",  token)
        jsonObj.put("towID" , id)

        return jsonObj.toString()
    }



    @Throws (IOException::class)
    override fun testShelf(locationCode: String, token: String): Boolean {
        connectionManager.setConnection(connectionURL)
        val res = connectionManager.post(getTestShelfJSON(locationCode, token))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        lastError = jsonRes.getString(JSON_ERROR_DESC)

        return resCode == RESPONSE_OK
    }

    private fun getTestShelfJSON(locationCode: String, token: String): String{
        val jsonObj = JSONObject()

        jsonObj.put("func",  "testPlace")
        jsonObj.put("token",  token)
        jsonObj.put("localization",  locationCode)

        return jsonObj.toString()
    }

    @Throws (IOException::class)
    override fun sendWare(wareCode: String, locationCode: String, token: String): Boolean {
        connectionManager.setConnection(connectionURL)
        val res = connectionManager.post(getSendWareJSON(wareCode, locationCode, token))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        lastError = jsonRes.getString(JSON_ERROR_DESC)

        return resCode == RESPONSE_OK

    }

    private fun getSendWareJSON(wareCode: String, locationCode: String, token: String): String{
        val jsonObj = JSONObject()

        jsonObj.put("func",  "addPlace")
        jsonObj.put("token",  token)
        jsonObj.put("localization",  locationCode)
        jsonObj.put("towQR",  wareCode)

        return jsonObj.toString()
    }

    @Throws (IOException::class)
    override fun getWare(qrCode: String, token: String): Ware? {
        connectionManager.setConnection(connectionURL)
        val res = connectionManager.post(getQRSearchJSON(qrCode, token))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)
        return if (resCode == RESPONSE_OK){

            val towID = jsonRes.getInt("towID")
            val lokalizacja = jsonRes.getString("lokalizacja")
            val indeks = jsonRes.getString("indeks")
            val nazwa = jsonRes.getString("nazwa")
            val isFoto = jsonRes.getBoolean("isFoto")
            val cena = jsonRes.getDouble("cena")
            val stanyMagaz = jsonRes.getJSONArray("stanyMagaz")

            val availabilities = ArrayList<Availability>()

            for (i in 0 until stanyMagaz.length()){
                val json = stanyMagaz.getJSONObject(i)

                val id = json.getInt("id")
                val magazyn = json.getString("magazyn")
                val ilosc = json.getDouble("ilosc")

                availabilities.add(Availability(id, magazyn, ilosc))
            }

            val ware = Ware(qrCode, towID, indeks, nazwa, isFoto, lokalizacja, cena)
            ware.availabilities = availabilities

            ware

        }else{
            lastError = jsonRes.getString(JSON_ERROR_DESC)
            null

        }


    }


    private fun getQRSearchJSON(qrCode : String, token: String): String{
        val jsonObj = JSONObject()

        jsonObj.put("func", "findTowar")
        jsonObj.put("token",  token)
        jsonObj.put("QR" , qrCode)

        return jsonObj.toString()
    }

    @Throws (IOException::class)
    override fun sendPhoto(ware: Ware, token: String): Boolean {
        connectionManager.setConnection(connectionURL)
        val res = connectionManager.post(getAddPhotoJSON(ware, token))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)
        return if (resCode == RESPONSE_OK){
            true
        }else{
            lastError = jsonRes.getString(JSON_ERROR_DESC)
            false
        }
    }

    private fun getAddPhotoJSON(ware : Ware, token: String): String{
        val jsonObj = JSONObject()

        jsonObj.put("func", "addFoto")
        jsonObj.put("towID" , ware.id)
        jsonObj.put("token",  token)
        jsonObj.put("foto" , getPhotoString(ware))

        return jsonObj.toString()
    }

    private fun getPhotoString(ware :Ware) : String{
       /* val file = File(ware.photoPath)
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.fromFile(file))*/
        val bitmap = ware.imageBitmap

    //    val rotatedBitmap = rotateBitmap(bitmap)
        val stream = ByteArrayOutputStream()
        val resized = resizeBitmap(bitmap!!, getPictureSize(context))
        resized.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()
        return Base64.encodeToString(image, Base64.DEFAULT)
    }

    private fun photoFromString(string : String) :  Bitmap{
        val imageBytes = Base64.decode(string, 0)

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }


    companion object{
        const val RESPONSE_OK = 0
        private val JSON_ERROR_CODE = "retCode"
        private val JSON_ERROR_DESC = "retOpis"

        const val MAX_PERCENT = 100f
        const val MIN_PERCENT = 10f

        private fun getPictureSize(context: Context) : Float{
            val prefSize: Float = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("picture_size", "0.1").toFloat()

            return prefSize
        }
    }
}