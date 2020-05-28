package pl.tysia.maggwarehouse.Presentation.UserInterface.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.CatalogAdapter
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.ICatalogable
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer.StringFilter

import pl.tysia.maggwarehouse.R
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CatalogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CatalogFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
abstract class CatalogFragment<T : ICatalogable> : Fragment(), TextWatcher, CatalogAdapter.ItemSelectedListener<T>  {
    private var catalogItems: ArrayList<T>? = null
    protected var adapter: CatalogAdapter<T>? = null
    private var filter: StringFilter? = null

    private var recyclerView: RecyclerView? = null
    private var searchEditText: EditText? = null

    protected abstract fun readCatalogItems(): ArrayList<T>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catalogItems = readCatalogItems()
        adapter = CatalogAdapter<T>(catalogItems!!)
        adapter?.addItemSelectedListener(this)

        val filterer = adapter?.filterer
        filter = StringFilter("")
        filterer?.addFilter(filter!!)


    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        searchEditText = view.findViewById(R.id.search_edit_text)

        recyclerView!!.adapter = adapter

        searchEditText!!.addTextChangedListener(this)

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager

        adapter?.filter()
        adapter?.notifyDataSetChanged()

        return view
    }

    override fun onResume() {
        super.onResume()

        adapter?.allItems?.clear()
        adapter?.addAll(readCatalogItems())
        adapter?.filter()
        adapter?.notifyDataSetChanged()

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {
        filter?.filteredString = s.toString()
        adapter?.filter()
        adapter?.notifyDataSetChanged()

    }



}
