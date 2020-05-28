package pl.tysia.maggwarehouse.BusinessLogic.Domain

enum class UserType (val typeName : String?) {
    ADMIN("ADMIN"),
    WORKER("WORKER");

    companion object {
        fun fromString(string: String?) : UserType?{
            return when (string){
                ADMIN.typeName -> ADMIN
                WORKER.typeName -> WORKER
                else ->  null
            }
        }

    }
}