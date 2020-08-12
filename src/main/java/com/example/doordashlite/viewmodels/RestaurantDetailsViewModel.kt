package com.example.doordashlite.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doordashlite.R
import com.example.doordashlite.models.Restaurant
import com.example.doordashlite.api.Restaurants
import com.example.doordashlite.models.DialogInfo
import com.example.doordashlite.utilties.ConnectivityUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantDetailsViewModel : ViewModel() {
    val restaurantsDetails = MutableLiveData<Restaurant>()
    val detailsLoadingIndicator = MutableLiveData<Boolean>()

    private val repoRetriever = Restaurants()
    private lateinit var connectivityManager: ConnectivityManager
    val detailDialog = MutableLiveData<DialogInfo>()
    val exitDetails = MutableLiveData<Boolean>()
    val openSettings = MutableLiveData<Boolean>()
    val displayNoImage = MutableLiveData<Int>()

    private val callback = object : Callback<Restaurant> {
        override fun onFailure(call: Call<Restaurant>?, t: Throwable?) {
            detailsLoadingIndicator.postValue(false)
            Log.e("MainActivity", "Problem calling Restaurant Detail API")
            if (!ConnectivityUtility.isConnected(connectivityManager)) {
                detailDialog.postValue(
                    DialogInfo(
                        R.string.no_internet_title,
                        R.string.no_internet_text,
                        R.string.no_internet_positive,
                        true,
                        R.string.no_internet_negative
                    )
                )
            } else {
                detailDialog.postValue(
                    DialogInfo(
                        R.string.no_details_title,
                        R.string.no_details_text,
                        R.string.no_details_positive,
                        false, 0
                    )
                )
            }
        }

        override fun onResponse(call: Call<Restaurant>?, response: Response<Restaurant>?) {
            detailsLoadingIndicator.postValue(false)
            response?.isSuccessful.let {
                if (response?.body() == null) {
                    detailDialog.postValue(
                        DialogInfo(
                            R.string.no_details_title,
                            R.string.no_details_text,
                            R.string.no_details_positive,
                            false, 0
                        )
                    )
                } else {
                    restaurantsDetails.postValue(response.body())
                }
            }
        }
    }

    fun loadData(applicationContext: Context?, restaurantsViewModel: RestaurantsViewModel) {
        connectivityManager =
            applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        detailsLoadingIndicator.postValue(true)
        repoRetriever.getDetails(restaurantsViewModel.getSelectedRestaurant(), callback)
    }

    fun processDialogSelections(dialogTitle: Int, positiveButtonSelected: Boolean) {
        if (dialogTitle == R.string.no_internet_title && positiveButtonSelected) {
            openSettings.postValue(true)
        } else {
            exitDetails.postValue(true)
        }
    }

    fun processLoadImageFailure() {
        displayNoImage.postValue(R.drawable.no_image_available)
    }
}