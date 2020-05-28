package pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer

import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.ICatalogable

class StringFilter(var filteredString : String?) : Filter<ICatalogable>(){


    override fun fitsFilter(item: ICatalogable): Boolean {
        return if (filteredString == null)
            true
        else
            item.getTitle().toLowerCase().contains(filteredString!!.toLowerCase())
    }

}