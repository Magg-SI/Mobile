package pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer

abstract class Filter<in T : IFilterable> {

    abstract fun fitsFilter(item: T): Boolean
}