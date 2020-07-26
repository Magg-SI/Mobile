package pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter;

import android.graphics.Bitmap;

import pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer.IFilterable;

public interface ICatalogable extends IFilterable {
    String getTitle();
    String getShortDescription();
    boolean isMarked();

}
