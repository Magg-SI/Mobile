package pl.tysia.maggwarehouse.Persistance

import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.BusinessLogic.Domain.UserType

class LoginServiceMock : LoginService{
    override fun changePassword(user: User, password: String): Boolean {
        return true
    }

    override fun authenticateUser(user: User): Int {
        user.type = UserType.WORKER
        return LoginService.OK
    }

}