package bj.vinylbrowser.common

import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bj.vinylbrowser.App
import bj.vinylbrowser.AppComponent
import bj.vinylbrowser.customviews.MyRecyclerView
import com.airbnb.epoxy.EpoxyController
import com.bluelinelabs.conductor.Controller

/**
 * Created by Josh Laird on 29/05/2017.
 */
abstract class BaseController : Controller() {
    override fun onAttach(view: View) {
//        setupComponent(App.appComponent)
        super.onAttach(view)
    }

    abstract fun setupComponent(appComponent: AppComponent)

    protected fun setupRecyclerView(recyclerView: MyRecyclerView, controller: EpoxyController) {
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = controller.adapter
    }

//    protected fun setupToolbar(toolbar: Toolbar, title: String) {
//        toolbar.title = title
//        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
//        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
//        getSupportActionBar()!!.setHomeButtonEnabled(true)
//    }
}