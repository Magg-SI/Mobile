package pl.tysia.maggwarehouse.Persistance

import pl.tysia.maggwarehouse.BusinessLogic.Domain.User

interface LoginService {
    fun authenticateUser(user: User) : Int
    fun changePassword(user : User, password : String) : Boolean


    companion object{
        const val OK = 0
        const val WRONG_PASSWORD = 1
        const val WRONG_LOGIN = 2
    }
}