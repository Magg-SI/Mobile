package pl.tysia.maggwarehouse.Presentation.UserInterface.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import pl.tysia.maggwarehouse.BusinessLogic.Domain.Ware
import pl.tysia.maggwarehouse.Presentation.UserInterface.Activities.ScannerActivity
import pl.tysia.maggwarehouse.Presentation.UserInterface.Activities.WareEditorActivity
import java.util.ArrayList

import pl.tysia.maggwarehouse.R


class WaresCatalog : CatalogFragment<Ware>() {

    override fun readCatalogItems(): ArrayList<Ware> {
        //val wareService = WareServiceMock()
        //return wareService.getWares(10,0)
        return ArrayList()

    }

    override fun onItemSelected(item: Ware) {
        val returnIntent = Intent(activity, WareEditorActivity::class.java)
        returnIntent.putExtra("scanner_result", item)
        startActivity(returnIntent)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = super.onCreateView(inflater, container, savedInstanceState)


        val scanButton = view?.findViewById<Button>(R.id.scanButton)
        scanButton?.setOnClickListener({ onAddSlabClicked() })

        return view
    }

    fun onAddSlabClicked() {
        val intent = Intent(activity, ScannerActivity::class.java)
        startActivity(intent)
    }


    companion object {


        fun newInstance(): WaresCatalog {

            return WaresCatalog()
        }
    }
}
