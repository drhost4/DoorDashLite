package com.example.doordashlite.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doordashlite.*
import com.example.doordashlite.adapters.RestaurantsListAdapter
import com.example.doordashlite.models.DialogInfo
import com.example.doordashlite.models.Location
import com.example.doordashlite.models.Restaurant
import com.example.doordashlite.viewmodels.RestaurantsViewModel
import kotlinx.android.synthetic.main.restaurant_list_layout.*

class RestaurantsFragment : Fragment() {

    private lateinit var viewModel: RestaurantsViewModel
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.restaurant_list_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.run {
            viewModel = ViewModelProviders.of(this).get(RestaurantsViewModel::class.java)
        }

        viewModel.restaurantsData.observe(
            this,
            Observer<List<Restaurant>> { restaurantsData -> displayRestaurants(restaurantsData) })

        viewModel.loadingIndicator.observe(this, Observer<Boolean> { display ->
            displayLoadingIndicator(display)
        })

        viewModel.displayDialog.observe(
            this,
            Observer<DialogInfo> { dialogInfo -> displayDialog(dialogInfo) })

        viewModel.openSettings.observe(
            this,
            Observer<Boolean> { startActivity(Intent(Settings.ACTION_SETTINGS)) })
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData(activity?.applicationContext, Location())
    }

    private fun displayRestaurants(restaurants: List<Restaurant>) {
        restaurantListRecycler.visibility = View.VISIBLE
        restaurantListRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        restaurantListRecycler.adapter =
            RestaurantsListAdapter(
                restaurants,
                viewModel
            )
    }

    private fun displayLoadingIndicator(display: Boolean) {
        restaurantListLoading.visibility = if (display) View.VISIBLE else View.GONE
    }

    private fun displayDialog(dialogInfo: DialogInfo) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(dialogInfo.notification)
            .setCancelable(false)
            .setPositiveButton(dialogInfo.positiveText) { dialog, id ->
                viewModel.processDialogSelections(dialogInfo.title, true)
            }

        if (dialogInfo.displayNegativeButton) {
            dialogBuilder.setNegativeButton(dialogInfo.negativeText) { dialog, _ ->
                viewModel.processDialogSelections(dialogInfo.title, false)
            }
        }

        dialog = dialogBuilder.create()
        dialog.setTitle(dialogInfo.title)
        dialog.show()
    }
}
