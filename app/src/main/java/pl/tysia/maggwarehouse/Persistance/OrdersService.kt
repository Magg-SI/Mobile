package pl.tysia.maggwarehouse.Persistance

import pl.tysia.maggwarehouse.BusinessLogic.Domain.Order
import pl.tysia.maggwarehouse.BusinessLogic.Domain.OrderedWare

interface OrdersService {
    fun getOrders(token: String) : ArrayList<Order>
    fun getOrder(token: String, orderId : Int) : ArrayList<OrderedWare>

    fun packOrder(token: String, orderedWare: OrderedWare) : Boolean
}