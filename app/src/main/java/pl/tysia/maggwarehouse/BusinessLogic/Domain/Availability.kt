package pl.tysia.maggwarehouse.BusinessLogic.Domain

import java.io.Serializable

data class Availability(var id : Int, var warehouse : String, var count : Double) : Serializable {
}