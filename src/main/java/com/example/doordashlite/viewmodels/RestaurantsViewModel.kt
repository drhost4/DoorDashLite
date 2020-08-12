package com.example.doordashlite.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doordashlite.R
import com.example.doordashlite.models.Location
import com.example.doordashlite.models.Restaurant
import com.example.doordashlite.api.Restaurants
import com.example.doordashlite.models.DialogInfo
import com.example.doordashlite.utilties.ConnectivityUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantsViewModel : ViewModel() {

    val restaurantsData = MutableLiveData<List<Restaurant>>()
    val loadingIndicator = MutableLiveData<Boolean>()
    val displayDetails = MutableLiveData<Boolean>()
    val displayDialog = MutableLiveData<DialogInfo>()
    val exitApp = MutableLiveData<Boolean>()
    val openSettings = MutableLiveData<Boolean>()

    private val repoRetriever = Restaurants()
    private var restaurants = ArrayList<Restaurant>()
    private var selectedRestaurant = -1L
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var currentLocation: Location

    private val callback = object : Callback<List<Restaurant>> {
        override fun onFailure(call: Call<List<Restaurant>>?, t: Throwable?) {
            loadingIndicator.postValue(false)
            if (!ConnectivityUtility.isConnected(connectivityManager)) {
                displayDialog.postValue(
                    DialogInfo(
                        R.string.no_internet_title,
                        R.string.no_internet_text,
                        R.string.no_internet_positive,
                        true,
                        R.string.no_internet_negative
                    )
                )
            } else {
                displayDialog.postValue(
                    DialogInfo(
                        R.string.no_restaurants_title,
                        R.string.no_restaurants_text,
                        R.string.no_restaurants_positive,
                        false, 0
                    )
                )
            }

            Log.e("MainActivity", "Problem calling Restaurants API}")
        }

        override fun onResponse(
            call: Call<List<Restaurant>>?,
            response: Response<List<Restaurant>>?
        ) {
            loadingIndicator.postValue(false)
            response?.isSuccessful.let {
                if (response?.body()?.size == 0) {
                    restaurantsData.postValue(restaurants)
                    displayDialog.postValue(
                        DialogInfo(
                            R.string.no_restaurants_title,
                            R.string.no_restaurants_text,
                            R.string.no_restaurants_positive,
                            false, 0
                        )
                    )
                } else {
                    restaurantsData.postValue(response?.body())
                    restaurants = response?.body() as ArrayList<Restaurant>
                }
            }

            if (response?.isSuccessful == false) {
                displayDialog.postValue(
                    DialogInfo(
                        R.string.no_restaurants_title,
                        R.string.no_restaurants_text,
                        R.string.no_restaurants_positive,
                        false, 0
                    )
                )
            }
        }
    }

    fun loadData(applicationContext: Context?, location: Location) {
        connectivityManager =
            applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        currentLocation = location
        if (restaurants.size > 0) {
            restaurantsData.postValue(restaurants)
        } else {
            loadingIndicator.postValue(true)
            repoRetriever.getRestaurants(location, callback)
        }
    }

    fun processSelection(position: Int) {
        selectedRestaurant = restaurants[position].id
        displayDetails.postValue(true)
    }

    fun getSelectedRestaurant(): Long {
        return selectedRestaurant
    }

    fun setSelectedRestaurant(restaurantId: Long) {
        selectedRestaurant = restaurantId
    }

    fun processDialogSelections(dialogTitle: Int, positiveButtonSelected: Boolean) {
        if (dialogTitle == R.string.no_internet_title && positiveButtonSelected) {
            openSettings.postValue(true)
        } else {
            exitApp.postValue(true)
        }
    }
}