package pl.tysia.maggwarehouse.Presentation.UserInterface.Fragments

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.tysia.maggwarehouse.Presentation.UserInterface.Activities.MainActivity
import pl.tysia.maggwarehouse.Presentation.UserInterface.Activities.WareScannerActivity

import pl.tysia.maggwarehouse.R

class ProductsScanFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val thisView = inflater.inflate(R.layout.fragment_products_scan, container, false)
        val back = thisView.findViewById<ConstraintLayout>(R.id.scan_background)

        back.setOnClickListener {
            val returnIntent = Intent(activity, WareScannerActivity::class.java)
            activity!!.startActivityForResult(returnIntent, MainActivity.SCANNER_REQUEST_CODE)
        }

        return thisView
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProductsScanFragment()
    }
}
