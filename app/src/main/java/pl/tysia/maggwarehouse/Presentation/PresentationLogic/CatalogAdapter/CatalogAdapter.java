package pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;

import pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer.CatalogFilterer;
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer.Filterer;


public abstract class CatalogAdapter<T extends ICatalogable> extends RecyclerView.Adapter<CatalogAdapter.CatalogItemViewHolder> {
    protected ArrayList<T> shownItems;
    protected ArrayList<T> allItems;

    protected Filterer filterer;

    protected T selectedItem;

    private ArrayList<ItemSelectedListener> listeners;
    protected ArrayList<EmptyListListener> emptyListeners;

    public abstract class CatalogItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CatalogItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public T getSelectedItem() {
        return selectedItem;
    }

    public Filterer getFilterer() {
        return filterer;
    }

    public void setFilterer(Filterer filterer) {
        this.filterer = filterer;
    }

    protected void onItemClick(View v, int adapterPosition){
        T item = shownItems.get(adapterPosition);

        selectedItem = item;
        notifyDataSetChanged();

        for (ItemSelectedListener listener : listeners) listener.onItemSelected(item);
    }

    public void addItemSelectedListener(ItemSelectedListener listener){
        listeners.add(listener);
    }

    public void addEmptyListener(EmptyListListener listener){
        emptyListeners.add(listener);
    }

    public void addItem(T item){
        allItems.add(0, item);
    }

    public void addAll(Collection<T> items){
        allItems.addAll(items);
    }


    public CatalogAdapter(ArrayList<T> items) {
        allItems = items;
        shownItems = new ArrayList<>();

        filterer = new CatalogFilterer<>(allItems, shownItems);

        listeners = new ArrayList<>();
        emptyListeners = new ArrayList<>();

    }

    public void filter(){
        filterer.filter();
    }


    @Override
    public int getItemCount() {
        return shownItems.size();
    }

    public interface ItemSelectedListener<T extends ICatalogable>{
        void onItemSelected(T item);
    }

  public interface EmptyListListener{
        void onListEmptied();
    }


}