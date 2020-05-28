package pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer

import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.ICatalogable
import java.util.ArrayList

class CatalogFilterer<T : ICatalogable>(basicList: ArrayList<T>, filteredList: ArrayList<T>) :
    Filterer<T>(basicList, filteredList)