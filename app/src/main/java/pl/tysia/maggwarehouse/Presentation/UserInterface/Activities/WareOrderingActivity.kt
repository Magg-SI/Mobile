package pl.tysia.maggwarehouse.Presentation.UserInterface.Activities

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_ware_ordering.*
import pl.tysia.maggwarehouse.BusinessLogic.Domain.OrderedWare
import pl.tysia.maggwarehouse.R

class WareOrderingActivity : AppCompatActivity(), TextWatcher {
    private lateinit var orderedWare: OrderedWare

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
    }

    public fun onSaveClicked(view: View){
        //TODO: send ware

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
}
