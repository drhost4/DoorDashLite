package com.example.doordashlite.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.doordashlite.R
import com.example.doordashlite.models.DialogInfo
import com.example.doordashlite.models.Restaurant
import com.example.doordashlite.viewmodels.RestaurantDetailsViewModel
import com.example.doordashlite.viewmodels.RestaurantsViewModel
import kotlinx.android.synthetic.main.restaurant_details.*
import java.text.NumberFormat

class RestaurantDetailFragment : Fragment() {

    private lateinit var restaurantsViewModel: RestaurantsViewModel
    private lateinit var detailViewModel: RestaurantDetailsViewModel
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.restaurant_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.run {
            restaurantsViewModel = ViewModelProviders.of(this).get(RestaurantsViewModel::class.java)
        }

        detailViewModel =
            ViewModelProviders.of(this).get(RestaurantDetailsViewModel::class.java)
        detailViewModel.restaurantsDetails.observe(this, Observer<Restaurant> { restaurant ->
            displayDetails(restaurant)
        })

        detailViewModel.detailsLoadingIndicator.observe(this, Observer<Boolean> { display ->
            displayLoadingIndicator(display)
        })

        detailViewModel.detailDialog.observe(
            this,
            Observer<DialogInfo> { dialogInfo -> displayDialog(dialogInfo) })

        detailViewModel.openSettings.observe(
            this,
            Observer<Boolean> { startActivity(Intent(Settings.ACTION_SETTINGS)) })

        detailViewModel.exitDetails.observe(
            this,
            Observer<Boolean> { activity?.onBackPressed() })

        detailViewModel.displayNoImage.observe(
            this,
            Observer<Int> { imageResourceId -> restaurantDetailLogo.setImageResource(imageResourceId) })

        detailViewModel.loadData(activity?.applicationContext, restaurantsViewModel)
    }

    private fun displayDetails(restaurant: Restaurant) {
        scrollView.visibility = View.VISIBLE
        Glide.with(context!!).load(restaurant.coverImageUrl)
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    detailViewModel.processLoadImageFailure()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            })
            .into(restaurantDetailLogo)

        restaurantDetailName.text = restaurant.name
        restaurantDetailDescription.text = restaurant.description
        restaurantDetailDeliveryFee.text =
            NumberFormat.getCurrencyInstance().format(restaurant.deliveryFee)
        restaurantDetailDeliveryTime.text = restaurant.status
    }

    private fun displayLoadingIndicator(display: Boolean) {
        restaurantDetailsLoading.visibility = if (display) View.VISIBLE else View.GONE
    }

    private fun displayDialog(dialogInfo: DialogInfo) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(dialogInfo.notification)
            .setCancelable(false)
            .setPositiveButton(dialogInfo.positiveText) { dialog, id ->
                detailViewModel.processDialogSelections(dialogInfo.title, true)
            }

        if (dialogInfo.displayNegativeButton) {
            dialogBuilder.setNegativeButton(dialogInfo.negativeText) { dialog, _ ->
                detailViewModel.processDialogSelections(dialogInfo.title, false)
            }
        }

        dialog = dialogBuilder.create()
        dialog.setTitle(dialogInfo.title)
        dialog.show()
    }
}
