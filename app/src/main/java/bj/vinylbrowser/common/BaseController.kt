package bj.vinylbrowser.common

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import bj.vinylbrowser.customviews.MyRecyclerView
import bj.vinylbrowser.main.MainComponent
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

    abstract fun setupComponent(mainComponent: MainComponent)

    protected fun setupRecyclerView(recyclerView: MyRecyclerView, controller: EpoxyController) {
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = controller.adapter
    }

    protected fun setupToolbar(toolbar: Toolbar, title: String) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        toolbar.title = title
    }
}

