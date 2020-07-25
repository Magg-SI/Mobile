package pl.tysia.maggwarehouse.Presentation.UserInterface.Fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
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
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import pl.tysia.maggwarehouse.BusinessLogic.Domain.Order
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.Persistance.OrdersService
import pl.tysia.maggwarehouse.Persistance.OrdersServiceImpl
import pl.tysia.maggwarehouse.Persistance.OrdersServiceMock
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.BasicCatalogAdapter
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.CatalogAdapter
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.ICatalogable
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer.StringFilter
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.okDialog
import pl.tysia.maggwarehouse.Presentation.UserInterface.Activities.OrderedWaresActivity
import pl.tysia.maggwarehouse.Presentation.UserInterface.Activities.ShelfScannerActivity
import pl.tysia.maggwarehouse.R
import java.io.IOException
import java.util.*

open class OrdersFragment : Fragment() , CatalogAdapter.ItemSelectedListener<Order>, TextWatcher {
    private lateinit var catalogItemsList: ArrayList<ICatalogable>
    private lateinit var adapter: CatalogAdapter<ICatalogable>
    private var filter: StringFilter? = null

    private lateinit var thisView : View

    private lateinit var recyclerView: RecyclerView
    //private lateinit var searchEditText: EditText

    private var getOrdersTask : GetOrdersTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        catalogItemsList = ArrayList()
        adapter =
            BasicCatalogAdapter(
                catalogItemsList
            )
        adapter.addItemSelectedListener(this)

        val filterer = adapter.filterer
        filter = StringFilter(null)
        filterer.addFilter(filter!!)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        thisView =  inflater.inflate(R.layout.fragment_orders, container, false)

        recyclerView = thisView.findViewById(R.id.recycler_view)
       // searchEditText = thisView.findViewById(R.id.search_edit_text)

        recyclerView.adapter = adapter

        //searchEditText.addTextChangedListener(this)

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager

        adapter.filter()
        adapter.notifyDataSetChanged()

        if (getOrdersTask == null){
            showProgress(true)
            getOrdersTask = GetOrdersTask()
            getOrdersTask?.execute()
        }

        return thisView
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            OrdersFragment()
    }

    override fun onItemSelected(item: Order?) {
        val returnIntent = Intent(activity, OrderedWaresActivity::class.java)
        returnIntent.putExtra(OrderedWaresActivity.ORDER, item)
        startActivity(returnIntent)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {
        filter?.filteredString = s.toString()
        adapter.filter()
        adapter.notifyDataSetChanged()

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        val fragmentLoading = thisView.findViewById<ProgressBar>(R.id.fragment_loading)
        val fragmentLayout = thisView.findViewById<FrameLayout>(R.id.fragment_layout)

        fragmentLayout.visibility = if (show) View.GONE else View.VISIBLE
        fragmentLayout.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 0 else 1).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    fragmentLayout.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

        fragmentLoading.visibility = if (show) View.VISIBLE else View.GONE
        fragmentLoading.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    fragmentLoading.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
    }

    inner class GetOrdersTask internal constructor() :
        AsyncTask<String, String, ArrayList<Order>>() {
        private val ordersService = OrdersServiceImpl(activity!!)
        private var exceptionOccurred = false

        override fun doInBackground(vararg params: String): ArrayList<Order>? {

           val user = User.getLoggedUser(activity!!)

            return try {
                return ordersService.getOrders(user!!.token!!)
            } catch (e: IOException) {
                exceptionOccurred = true
                null
            }

        }

        override fun onPostExecute(result: ArrayList<Order>?) {
            getOrdersTask = null
            showProgress(false)

            when {
                result != null -> {
                    adapter.addAll(result as Collection<ICatalogable>?)
                    adapter.filter()
                    adapter.notifyDataSetChanged()
                }
                exceptionOccurred -> {
                    okDialog(
                        getString(R.string.connection_error),
                        getString(R.string.connectoin_error_long_message),
                        activity!!
                    )
                }
                else -> {
                    Toast.makeText(
                        activity,
                        "Nie udało się pobrać zamówień",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }

        override fun onCancelled() {
            getOrdersTask = null
            showProgress(false)
        }
    }
}
