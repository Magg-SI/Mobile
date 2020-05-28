package pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer

import java.util.*

abstract class Filterer<T : IFilterable>(var basicList: ArrayList<T>, var filteredList: ArrayList<T>) {

    private var filters: LinkedList<Filter<T>>

    init {

        this.filters = LinkedList()
    }

    fun addFilter(filter: Filter<T>) {
        filters.add(filter)
        filter()
    }

    fun removeFilter(filter: Filter<T>) {
        filters.remove(filter)
        filter()
    }

    fun notifyFilterChange() {
        filter()
    }

    fun filter(): ArrayList<T> {
        filteredList.clear()

        if (filters.isEmpty())
            filteredList.addAll(basicList)
        else
            for (filterable in basicList) {
                var fits = true

                for (filter in filters) {
                    if (!filter.fitsFilter(filterable))
                        fits = false
                    break
                }

                if (fits)
                    filteredList.add(filterable)

            }

        return filteredList
    }
}
