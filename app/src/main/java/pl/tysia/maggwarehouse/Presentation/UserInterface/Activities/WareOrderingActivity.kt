package pl.tysia.maggwarehouse.Presentation.UserInterface.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_ware_ordering.*
import pl.tysia.maggwarehouse.BusinessLogic.Domain.OrderedWare
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.Persistance.OrdersServiceImpl
import pl.tysia.maggwarehouse.Persistance.OrdersServiceMock
import pl.tysia.maggwarehouse.Presentation.PresentationLogic.okDialog
import pl.tysia.maggwarehouse.R
import java.io.IOException

class WareOrderingActivity : AppCompatActivity(), TextWatcher {
    private lateinit var orderedWare: OrderedWare

    private var sendOrderTask : SendOrderTask? = null

    companion object{
        public const val ORDERED_WARE = "pl.tysia.maggwarehouse.ordered_ware"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ware_ordering)

        orderedWare = intent.getSerializableExtra(ORDERED_WARE) as OrderedWare

        displayWare()

        next_et.addTextChangedListener(this)
        packed_et.addTextChangedListener(this)
        cancelled_et.addTextChangedListener(this)
    }

    private fun displayWare(){
        index_tv.text = orderedWare.index
        name_tv.text = orderedWare.productName
        location_tv.text = this.orderedWare.location

        ordered_tv.text = orderedWare.orderedNumber.toString()
        available_tv.text = orderedWare.availability.toString()

        cancelled_et.setText(orderedWare.cancelledNumber.toString())
        packed_et.setText(orderedWare.packedNumber.toString())
        next_et.setText(orderedWare.postponedNumber.toString())
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val cancelled = cancelled_et.text.let { if (it.isNullOrEmpty())  0.0 else it.toString().toDouble() }
        val next = next_et.text.let { if (it.isNullOrEmpty())  0.0 else it.toString().toDouble() }
        val packed = packed_et.text.let { if (it.isNullOrEmpty())  0.0 else it.toString().toDouble() }

        val sum = cancelled + next + packed
        total_tv.text = sum.toString()

        if (sum > orderedWare.availability || sum < orderedWare.orderedNumber){
            total_tv.setTextColor(Color.RED)
            save_button.isEnabled = false
        }else{
            total_tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            save_button.isEnabled = true
        }
    }

    public fun onPackAllClicked(view: View){
        packed_et.setText(orderedWare.orderedNumber.toString())
        cancelled_et.setText(0.toString())
        next_et.setText(0.toString())

    }
    public fun onOrderAllClicked(view: View){
        next_et.setText(orderedWare.orderedNumber.toString())
        packed_et.setText(0.toString())
        cancelled_et.setText(0.toString())

    }
    public fun onCancelAllClicked(view: View){
        cancelled_et.setText(orderedWare.orderedNumber.toString())
        packed_et.setText(0.toString())
        next_et.setText(0.toString())

    }

    private fun getOrderedWare() : OrderedWare{
        val cancelled = cancelled_et.text.let { if (it.isNullOrEmpty())  0.0 else it.toString().toDouble() }
        val next = next_et.text.let { if (it.isNullOrEmpty())  0.0 else it.toString().toDouble() }
        val packed = packed_et.text.let { if (it.isNullOrEmpty())  0.0 else it.toString().toDouble() }

        orderedWare.postponedNumber = next
        orderedWare.packedNumber = packed
        orderedWare.cancelledNumber = cancelled

        return orderedWare
    }

    public fun onSaveClicked(view: View){
        if (sendOrderTask == null){
            sendOrderTask = SendOrderTask(getOrderedWare())
            sendOrderTask!!.execute()
        }
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


    inner class SendOrderTask internal constructor(private val ware: OrderedWare) :
        AsyncTask<String, String, Boolean>() {
        private var exceptionOccurred = false
        private val ordersService = OrdersServiceImpl(this@WareOrderingActivity)

        override fun doInBackground(vararg params: String): Boolean? {

            val user = User.getLoggedUser(this@WareOrderingActivity)

            return try {
                ordersService.packOrder(user!!.token!!, ware)
            }catch (e : IOException){
                exceptionOccurred = true
                false
            }
        }

        override fun onPostExecute(result: Boolean?) {
            sendOrderTask = null
            showProgress(false)

            when {
                result == true -> {
                    Toast.makeText(this@WareOrderingActivity, "Zapisano pomyślnie", Toast.LENGTH_LONG).show()

                    setResult(Activity.RESULT_OK)
                    finish()
                }
                exceptionOccurred -> okDialog(getString(R.string.connection_error), getString(R.string.connectoin_error_long_message), this@WareOrderingActivity)
                else -> Toast.makeText(this@WareOrderingActivity, "Nie udało się zapisać", Toast.LENGTH_LONG).show()
            }
        }

        override fun onCancelled() {
            sendOrderTask = null
            showProgress(false)
        }
    }
}
