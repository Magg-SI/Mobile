package pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter

import pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer.IFilterable

public interface ICatalogable : IFilterable{
    abstract fun getTitle(): String
    abstract fun getShortDescription(): String
    abstract fun getImageResource(): Int

    abstract fun getQuantityOrdered(): Int
    abstract fun setQuantityOrdered(quantityOrdered: Int)
    abstract fun getQuantityTaken(): Int
    abstract fun setQuantityTaken(quantityTaken: Int)
}