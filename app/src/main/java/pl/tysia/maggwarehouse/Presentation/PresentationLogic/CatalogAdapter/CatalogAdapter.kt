package pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer.CatalogFilterer
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer.Filterer


import pl.tysia.maggwarehouse.R

class CatalogAdapter<T : ICatalogable>(val allItems: ArrayList<T>) :
    RecyclerView.Adapter<CatalogAdapter<T>.CatalogItemViewHolder>() {

    protected var shownItems: ArrayList<T>

    var filterer: Filterer<T>? = null

    private var selectedItem: T? = null
        private set

    private val listeners: ArrayList<ItemSelectedListener<T>>

    inner class CatalogItemViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        internal var back: View
        internal var image: ImageView
        internal var title: TextView
        internal var description: TextView

        internal var numberPicker: EditText
        internal var numberPicker2: EditText
        internal var numberPickerMinus2: Button
        internal var numberPickerPlus2: Button
        internal var numberPickerMinus: Button
        internal var numberPickerPlus: Button
        internal var deleteButton: ImageButton

        internal var orderTV: TextView
        internal var orderedNumberTV: TextView

        init {

            back = v.findViewById(R.id.back_view)
            image = v.findViewById(R.id.image_view)
            title = v.findViewById(R.id.title_text_view)
            description = v.findViewById(R.id.description_text_view)
            numberPicker = v.findViewById(R.id.numberPicker)
            numberPickerMinus = v.findViewById(R.id.numberPickerMinus)
            numberPickerPlus = v.findViewById(R.id.numberPickerPlus)
            numberPicker2 = v.findViewById(R.id.numberPicker2)
            numberPickerMinus2 = v.findViewById(R.id.numberPickerMinus2)
            numberPickerPlus2 = v.findViewById(R.id.numberPickerPlus2)
            orderTV = v.findViewById(R.id.orderTV)
            orderedNumberTV = v.findViewById(R.id.orderedNumberTV)
            deleteButton = v.findViewById(R.id.deleteButton)

            v.setOnClickListener(this)

            deleteButton.setOnClickListener {
                allItems.removeAt(adapterPosition)
                filter()
                notifyDataSetChanged()
            }


            numberPickerPlus2.setOnClickListener {
                val n = Integer.valueOf(numberPicker2.text.toString())
                numberPicker.setText(Integer.toString(n + 1))
                shownItems[adapterPosition].setQuantityOrdered(n + 1)
                notifyDataSetChanged()
            }

            numberPickerMinus2.setOnClickListener {
                val n = Integer.valueOf(numberPicker2.text.toString())
                if (n > 0) {
                    numberPicker.setText(Integer.toString(n - 1))
                    shownItems[adapterPosition].setQuantityOrdered(n - 1)
                    notifyDataSetChanged()
                }
            }

            numberPickerPlus.setOnClickListener {
                val n = Integer.valueOf(numberPicker.text.toString())
                numberPicker.setText(Integer.toString(n + 1))
                shownItems[adapterPosition].setQuantityTaken(n + 1)
                notifyDataSetChanged()
            }

            numberPickerMinus.setOnClickListener {
                val n = Integer.valueOf(numberPicker.text.toString())
                if (n > 0) {
                    numberPicker.setText(Integer.toString(n - 1))
                    shownItems[adapterPosition].setQuantityTaken(n - 1)
                    notifyDataSetChanged()
                }
            }

            orderTV.setOnClickListener {
                shownItems[adapterPosition].setQuantityOrdered(1)
                notifyDataSetChanged()
            }


        }


        override fun onClick(v: View) {
            val pos = adapterPosition
            onItemClick(v, pos)
        }

    }

    private fun onItemClick(v: View, adapterPosition: Int) {
        val item = shownItems[adapterPosition]

        selectedItem = item
        notifyDataSetChanged()

        for (listener in listeners) listener.onItemSelected(item)
    }

    fun addItemSelectedListener(listener: ItemSelectedListener<T>) {
        listeners.add(listener)
    }

    fun addItem(item: T) {
        allItems.add(item)
    }

    fun addAll(items: Collection<T>) {
        allItems.addAll(items)
    }

    init {
        shownItems = ArrayList()

        filterer = CatalogFilterer(allItems, shownItems)

        listeners = ArrayList()

    }

    fun filter() {
        filterer!!.filter()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogItemViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_catalog, parent, false)

        v.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

        return CatalogItemViewHolder(v)
    }

    override fun onBindViewHolder(CatalogItemViewHolder: CatalogItemViewHolder, i: Int) {
        val item = shownItems[i]

        CatalogItemViewHolder.title.setText(item.getTitle())

        if (item.getQuantityOrdered() > 0) {
            CatalogItemViewHolder.numberPicker2.setVisibility(View.VISIBLE)
            CatalogItemViewHolder.numberPicker2.setText(Integer.toString(item.getQuantityOrdered()))
            CatalogItemViewHolder.numberPickerMinus2.setVisibility(View.VISIBLE)
            CatalogItemViewHolder.numberPickerPlus2.setVisibility(View.VISIBLE)
            CatalogItemViewHolder.orderTV.setVisibility(View.GONE)
            CatalogItemViewHolder.orderedNumberTV.setVisibility(View.VISIBLE)

        } else {
            CatalogItemViewHolder.numberPicker2.setVisibility(View.INVISIBLE)
            CatalogItemViewHolder.numberPickerMinus2.setVisibility(View.INVISIBLE)
            CatalogItemViewHolder.numberPickerPlus2.setVisibility(View.INVISIBLE)
            CatalogItemViewHolder.orderTV.setVisibility(View.VISIBLE)
            CatalogItemViewHolder.orderedNumberTV.setVisibility(View.INVISIBLE)
        }

        CatalogItemViewHolder.description.setText(item.getShortDescription())

        CatalogItemViewHolder.numberPicker.setText(Integer.toString(item.getQuantityTaken()))

        val res = item.getImageResource()
        if (res > 0) {
            CatalogItemViewHolder.image.setVisibility(View.VISIBLE)
            CatalogItemViewHolder.image.setImageResource(res)
        } else
            CatalogItemViewHolder.image.setVisibility(View.GONE)

    }

    override fun getItemCount(): Int {
        return shownItems.size
    }

    interface ItemSelectedListener<T : ICatalogable> {
        fun onItemSelected(item: T)
    }


}