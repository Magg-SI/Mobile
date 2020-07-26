package pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import pl.tysia.maggwarehouse.BusinessLogic.Domain.Order;
import pl.tysia.maggwarehouse.R;

public class BasicCatalogAdapter extends CatalogAdapter<ICatalogable> {

    public BasicCatalogAdapter(ArrayList<ICatalogable> items) {
        super(items);
    }

    public class BasicViewHolder extends CatalogAdapter.CatalogItemViewHolder {
        TextView title;
        TextView description;
        View back;
        ImageButton deleteButton;

        BasicViewHolder(View v) {
            super(v);

            title = v.findViewById(R.id.title_tv);
            description = v.findViewById(R.id.description_tv);
            back = v.findViewById(R.id.back);
            deleteButton = v.findViewById(R.id.delete_button);


            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            onItemClick(v, pos);
        }

    }

    @NonNull
    @Override
    public CatalogItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.simple_list_item, viewGroup, false);

        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        BasicViewHolder vh = new BasicViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogAdapter.CatalogItemViewHolder catalogItemViewHolder, int i) {
        ICatalogable item = shownItems.get(i);

        BasicViewHolder wareViewHolder = (BasicViewHolder) catalogItemViewHolder;

        wareViewHolder.title.setText(item.getTitle());


        if (item.isMarked()){
            wareViewHolder.description.setText(item.getShortDescription() + "\nzapakowane");

            wareViewHolder.title.setEnabled(false);
            wareViewHolder.description.setEnabled(false);
        }else{
            wareViewHolder.description.setText(item.getShortDescription());

        }

    }
}
