package pl.tysia.maggwarehouse.BusinessLogic.Domain

import android.content.Context
import android.preference.PreferenceManager

class User(var login: String) {
    var password : String? = null
    var type : UserType? = null
    var locker : String? = null
    var id : Int? = null
    var token : String? = null;

    constructor(login : String, password : String) : this(login) {
        this.password = password
    }

    constructor(login: String, id: Int, token : String) : this(login){
        this.id = id
        this.token = token
    }

    fun setLogged(context : Context){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(LOGIN, login)
        editor.putInt(ID, id!!)
        editor.putString(TOKEN, token)
      //  editor.putString(LOCKER, locker)
      //  editor.putString(TYPE, type?.typeName)
        editor.commit()
    }


    companion object {
        const val LOGIN = "logged_user_login"
        const val ID = "logged_user_id"
        const val LOCKER = "logged_user_locker"
        const val TYPE = "logged_user_type"
        const val TOKEN = "logged_user_token"

        fun getLoggedUser(context : Context) : User?{
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val login = sharedPreferences.getString(LOGIN, null)
            val token = sharedPreferences.getString(TOKEN, null)
            val id = sharedPreferences.getInt(ID, -1)
           // val locker = sharedPreferences.getString(LOCKER, null)
           // val typeStr = sharedPreferences.getString(TYPE, null)
          //  val type: UserType? = UserType.fromString(sharedPreferences.getString(TYPE, null))

            return if (login != null && id>=0 && token!= null )
                User(login, id, token) else
                null
        }

        fun logout(context : Context){
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()

            editor.putString(LOGIN, null)
            editor.putString(TOKEN, null)
            editor.putInt(ID, -1)
          //  editor.putString(LOCKER, null)
          //  editor.putString(TYPE, null)
            editor.commit()
        }
    }
}