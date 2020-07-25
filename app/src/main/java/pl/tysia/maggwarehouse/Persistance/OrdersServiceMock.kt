package pl.tysia.maggwarehouse.Persistance

import pl.tysia.maggwarehouse.BusinessLogic.Domain.Order
import pl.tysia.maggwarehouse.BusinessLogic.Domain.OrderedWare

class OrdersServiceMock : OrdersService {
    override fun getOrders(token: String): ArrayList<Order> {

        return ArrayList<Order>().apply {
            for(i in 0..10)
                add(Order(i, "Magazyn $i", "12.05.2020", i.toString(), "Komentarz"))
        }
    }

    override fun getOrder(token: String, orderId : Int): ArrayList<OrderedWare> {
        return ArrayList<OrderedWare>().apply {
            for(i in 0..10)
                add(OrderedWare(i, "Indeks $i", "Produkt $i", "Lokalizacja $i","qrCode$i" ,i.toDouble(), i.toDouble(), i.toDouble(), i.toDouble(), (i*10).toDouble()))
        }
    }

    override fun packOrder(token: String, orderedWare: OrderedWare): Boolean {
        return true
    }
}