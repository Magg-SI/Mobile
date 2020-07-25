package pl.tysia.maggwarehouse.Persistance

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import pl.tysia.maggwarehouse.BusinessLogic.Domain.Order
import pl.tysia.maggwarehouse.BusinessLogic.Domain.OrderedWare
import pl.tysia.maggwarehouse.Persistance.Connection.URLConnectionManagerImpl
import java.io.IOException

class OrdersServiceImpl(context: Context) : OrdersService {
    private val connectionManager = URLConnectionManagerImpl()
    private val connectionURL = connectionManager.getConnectionURL(context)

    companion object{
        const val RESPONSE_OK = 0
        private val JSON_ERROR_CODE = "retCode"
        private val JSON_ERROR_DESC = "retOpis"

    }

    @Throws (IOException::class)
    override fun getOrders(token: String): ArrayList<Order> {
        connectionManager.setConnection(connectionURL)
        val res = connectionManager.post(getGetGetOrdersJSON(token))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val jsonArray = jsonRes.getJSONArray("listaZamow")

        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        val result = ArrayList<Order>()

        if (resCode == RESPONSE_OK)
        for (i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(i)

            val dokID = jsonObject.getInt("dokID")
            val magazyn = jsonObject.optString("magazyn", "brak")
            val dataDok = jsonObject.optString("dataDok", "brak")
            val nrDok = jsonObject.optString("nrDok", "brak")
            val uwagi = jsonObject.optString("uwagi", "brak")

            result.add(Order(dokID, magazyn, dataDok, nrDok, uwagi))

        }

        return result

    }

    private fun getGetGetOrdersJSON(token: String): String{
        val jsonObj = JSONObject()

        jsonObj.put("func",  "listaZamow")
        jsonObj.put("token",  token)

        return jsonObj.toString()
    }

    override fun getOrder(token: String, orderId: Int): ArrayList<OrderedWare> {
        connectionManager.setConnection(connectionURL)
        val res = connectionManager.post(getGetOrderJSON(token, orderId))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val jsonArray = jsonRes.getJSONArray("readOneZamow")

        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        val result = ArrayList<OrderedWare>()

        if (resCode == RESPONSE_OK)
        for (i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(i)

            val pozyID = jsonObject.getInt("pozyID")
            val indeks = jsonObject.optString("indeks", "brak")
            val nazwa = jsonObject.optString("nazwa", "brak")
            val lokalizacja = jsonObject.optString("lokalizacja", "brak")
            val kodQR = jsonObject.optString("kodQR", "brak")
            val ilZamow = jsonObject.optDouble("ilZamow", 0.0)
            val ilZapak = jsonObject.optDouble("ilZapak", 0.0)
            val ilNext = jsonObject.optDouble("ilNext", 0.0)
            val ilAnul = jsonObject.optDouble("ilAnul", 0.0)
            val stanMagaz = jsonObject.optDouble("stanMag", 0.0)

            result.add(OrderedWare(pozyID, indeks, nazwa, lokalizacja, kodQR, ilZamow, ilZapak, ilNext, ilAnul, stanMagaz))

        }

        return result
    }

    private fun getGetOrderJSON(token: String, orderId: Int): String{
        val jsonObj = JSONObject()

        jsonObj.put("func",  "readOneZamow")
        jsonObj.put("token",  token)
        jsonObj.put("dokID",  orderId)

        return jsonObj.toString()
    }

    override fun packOrder(token: String, orderedWare: OrderedWare): Boolean {
        connectionManager.setConnection(connectionURL)
        val res = connectionManager.post(getPackOrderJSON(token, orderedWare))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        return resCode== RESPONSE_OK
    }

    private fun getPackOrderJSON(token: String, orderedWare: OrderedWare): String{
        val jsonObj = JSONObject()

        jsonObj.put("func", "zapakOnePoz")
        jsonObj.put("token", token)
        jsonObj.put("pozyID", orderedWare.positionID)
        jsonObj.put("ilZapak", orderedWare.packedNumber)
        jsonObj.put("ilNext", orderedWare.postponedNumber)
        jsonObj.put("ilAnul", orderedWare.cancelledNumber)

        return jsonObj.toString()
    }

}