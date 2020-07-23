package pl.tysia.maggwarehouse.Persistance

import android.content.Context
import org.json.JSONObject
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.Persistance.Connection.URLConnectionManagerImpl

class LoginServiceImpl(private val context : Context) : LoginService {
    private val connectionManager = URLConnectionManagerImpl()


    override fun changePassword(user: User, password: String): Boolean {
        connectionManager.setConnection(NetAddressManager.getConnectionURL(context))
        val res= connectionManager.post(getChangePasswordJSON(user, password))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        return resCode == LoginService.OK
    }


    private fun getChangePasswordJSON(user : User, password: String) : String{
        val jsonObj = JSONObject()

        jsonObj.put("func", "changePwd")
        jsonObj.put("ID" , user.id)
        jsonObj.put("token",  user.token)
        jsonObj.put("newPwd", password)

        return jsonObj.toString()
    }

    override fun authenticateUser(user: User): Int {
        connectionManager.setConnection(NetAddressManager.getConnectionURL(context))
        val res= connectionManager.post(getAuthenticateJSON(user))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        if (resCode == 0) {
            val usId = jsonRes.getString(USER_ID)
            val usTk = jsonRes.getString(TOKEN)
            user.id = usId.toInt()
            user.token = usTk
            user.setLogged(context)
        }

        return when(resCode){
            0 -> LoginService.OK
            1 -> LoginService.WRONG_LOGIN
            2 -> LoginService.WRONG_PASSWORD
            else -> -1
        }
    }

    private fun getAuthenticateJSON(user : User) : String{
        val jsonObj = JSONObject()

        jsonObj.put("func", "login")
        jsonObj.put("login" , user.login)
        jsonObj.put("password", user.password)

        return jsonObj.toString()
    }

    companion object{
        private val JSON_ERROR_CODE = "retCode"
        private val USER_ID = "userID"
        private val TOKEN = "token"

    }
}