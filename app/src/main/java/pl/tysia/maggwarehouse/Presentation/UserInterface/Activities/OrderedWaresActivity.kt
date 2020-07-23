package pl.tysia.maggwarehouse.Presentation.UserInterface.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_ordered_products.*
import pl.tysia.maggwarehouse.BusinessLogic.Domain.Order
import pl.tysia.maggwarehouse.BusinessLogic.Domain.OrderedWare
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.Persistance.OrdersServiceMock
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.CatalogAdapter
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.BasicCatalogAdapter
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.CatalogAdapter.ICatalogable
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.Filterer.StringFilter
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.okDialog
import pl.tysia.maggwarehouse.R
import java.io.IOException
import java.util.ArrayList

class OrderedWaresActivity : AppCompatActivity() , CatalogAdapter.ItemSelectedListener<OrderedWare>,
    TextWatcher {
    private lateinit var catalogItemsList: ArrayList<ICatalogable>
    private lateinit var adapter: CatalogAdapter<ICatalogable>
    private var filter: StringFilter? = null

    private lateinit var recyclerView: RecyclerView
    //private lateinit var searchEditText: EditText

    private var getOrderedWaresTask : GetOrderedWaresTask? = null

    private lateinit var order : Order

    companion object{
        public const val ORDER = "pl.tysia.maggwarehouse.order"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordered_products)

        order = intent.getSerializableExtra(ORDER) as Order

        catalogItemsList = ArrayList()
        adapter =
            BasicCatalogAdapter(
                catalogItemsList
            )
        adapter.addItemSelectedListener(this)

        val filterer = adapter.filterer
        filter = StringFilter(null)
        filterer.addFilter(filter!!)

        recyclerView = findViewById(R.id.wares_recycler)
        //searchEditText = findViewById(R.id.search_edit_text)

        recyclerView.adapter = adapter

        //searchEditText.addTextChangedListener(this)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager

        order_desc_tv.text = order.shortDescription
        order_title_tv.text = order.title


        if (getOrderedWaresTask == null){
            showProgress(true)
            getOrderedWaresTask = GetOrderedWaresTask()
            getOrderedWaresTask?.execute()
        }


    }

    override fun onItemSelected(item: OrderedWare?) {
        val intent = Intent(this@OrderedWaresActivity, WareOrderingActivity::class.java)
        intent.putExtra(WareOrderingActivity.ORDERED_WARE, item)
        startActivity(intent)
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


        form.visibility = if (show) View.GONE else View.VISIBLE
        form.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 0 else 1).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    form.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        progressBar.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progressBar.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
    }

    inner class GetOrderedWaresTask internal constructor() :
        AsyncTask<String, String, ArrayList<OrderedWare>>() {
        private val ordersService = OrdersServiceMock()
        private var exceptionOccurred = false


        override fun doInBackground(vararg params: String): ArrayList<OrderedWare>? {

            val user = User.getLoggedUser(this@OrderedWaresActivity)

            return try {
                return ordersService.getOrder(user!!.token!!, order.id)
            } catch (e: IOException) {
                exceptionOccurred = true
                null
            }

        }

        override fun onPostExecute(result: ArrayList<OrderedWare>?) {
            getOrderedWaresTask = null
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
                        this@OrderedWaresActivity
                    )
                }
                else -> {
                    Toast.makeText(
                        this@OrderedWaresActivity,
                        "Nie udało się pobrać zamówienia",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

        }

        override fun onCancelled() {
            getOrderedWaresTask = null
            showProgress(false)
        }
    }
}
