package com.example.doordashlite

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.doordashlite.models.DialogInfo
import com.example.doordashlite.models.Location
import com.example.doordashlite.models.Restaurant
import com.example.doordashlite.viewmodels.RestaurantDetailsViewModel
import com.example.doordashlite.viewmodels.RestaurantsViewModel

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.doordashlite", appContext.packageName)
    }

    // Validate data returned by API for restaurant:  1 Oz Coffee
    @Test
    fun restaurantsTest() {
        val viewModel =
            RestaurantsViewModel()
        viewModel.loadData(InstrumentationRegistry.getInstrumentation().targetContext, Location())
        TimeUnit.MILLISECONDS.sleep(15000)
        val data = viewModel.restaurantsData.value as ArrayList<Restaurant>
        var firstRestaurant = data[0]
        for (restaurant in data) {
            if (restaurant.name == "1 Oz Coffee") {
                firstRestaurant = restaurant
                break
            }
        }

        assertEquals(firstRestaurant.id, 969948)
        assertEquals(
            firstRestaurant.coverImageUrl,
            "https://cdn.doordash.com/static/img/restaurants/covers/1ozcoffee-cover.png"
        )
        assertEquals(firstRestaurant.name, "1 Oz Coffee")
        assertEquals(firstRestaurant.description, "1 Oz Coffee")
        assertEquals(firstRestaurant.deliveryFee, 0.0, 0.001)
    }

    // Validate no results returned by API
    @Test
    fun noRestaurantsTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel =
            RestaurantsViewModel()
        viewModel.loadData(
            context,
            Location(0.0, 0.0)
        )
        TimeUnit.MILLISECONDS.sleep(15000)
        val data = viewModel.restaurantsData.value as ArrayList<Restaurant>
        assertEquals(data.size, 0)

        // Validate that the no available restaurants has been requested by the view model.
        val dialogDisplayed = viewModel.displayDialog.value as DialogInfo
        assertEquals(context.getString(dialogDisplayed.title), "No Available Restaurants")

        // Validate that selecting the "Cancel" button on the dialog will cause the view model to set the exit app
        // live data.  This will cause the application to exit.
        viewModel.processDialogSelections(R.string.no_restaurants_title, true)
        TimeUnit.MILLISECONDS.sleep(5000)
        assertEquals(viewModel.exitApp.value, true)
    }

    // Validate selected restaurant
    @Test
    fun selectionTest() {
        val viewModel =
            RestaurantsViewModel()
        viewModel.loadData(
            InstrumentationRegistry.getInstrumentation().targetContext,
            Location()
        )
        TimeUnit.MILLISECONDS.sleep(15000)
        val data = viewModel.restaurantsData.value as ArrayList<Restaurant>
        var index = 0
        for (restaurant in data) {
            if (restaurant.name == "1 Oz Coffee") {
                viewModel.processSelection(index)
                assertEquals(viewModel.getSelectedRestaurant(), 969948)
                break
            }

            index++
        }
    }

    @Test
    fun testRestaurantsListNoInternetDialog() {
        val viewModel =
            RestaurantsViewModel()
        // Validate that selecting the "Cancel" button on the dialog will cause the view model to set the exit details
        // live data.  This will cause the details fragment to exit.
        viewModel.processDialogSelections(R.string.no_internet_title, false)
        TimeUnit.MILLISECONDS.sleep(5000)
        assertEquals(viewModel.exitApp.value, true)

        // Validate that selecting the "Check Internet" button on the dialog will cause the view model to set the exit details
        // live data.  This will cause the details fragment to exit.
        viewModel.processDialogSelections(R.string.no_internet_title, true)
        TimeUnit.MILLISECONDS.sleep(5000)
        assertEquals(viewModel.openSettings.value, true)
    }

    // Validate details  returned by API for 1 Oz Coffee restaurant
    @Test
    fun restaurantDetailTest() {
        val restaurantsViewModel =
            RestaurantsViewModel()
        restaurantsViewModel.setSelectedRestaurant(969948)
        val viewModel =
            RestaurantDetailsViewModel()
        viewModel.loadData(
            InstrumentationRegistry.getInstrumentation().targetContext,
            restaurantsViewModel
        )
        TimeUnit.MILLISECONDS.sleep(15000)
        val data = viewModel.restaurantsDetails.value as Restaurant
        Log.e("check this", "restaurant: $data")
        assertEquals(data.id, 969948)
        assertEquals(
            data.coverImageUrl,
            "https://cdn.doordash.com/static/img/restaurants/covers/1ozcoffee-cover.png"
        )
        assertEquals(data.name, "1 Oz Coffee")
        assertEquals(data.description, "1 Oz Coffee")
        assertEquals(data.deliveryFee, 0.0, 0.001)
    }

    // Validate no results returned by details API
    @Test
    fun noRestaurantsDetailTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val restaurantsViewModel =
            RestaurantsViewModel()
        restaurantsViewModel.setSelectedRestaurant(0)
        val viewModel =
            RestaurantDetailsViewModel()
        viewModel.loadData(
            context,
            restaurantsViewModel
        )
        TimeUnit.MILLISECONDS.sleep(15000)
        val dialogDisplayed = viewModel.detailDialog.value as DialogInfo
        assertEquals(context.getString(dialogDisplayed.title), "Restaurant Details Not Available")

        // Validate that selecting the "Cancel" button on the dialog will cause the view model to set the exit details
        // live data.  This will cause the details fragment to exit.
        viewModel.processDialogSelections(R.string.no_details_title, true)
        TimeUnit.MILLISECONDS.sleep(5000)
        assertEquals(viewModel.exitDetails.value, true)
    }

    @Test
    fun testDetailNoInternetDialog() {
        val viewModel =
            RestaurantDetailsViewModel()
        // Validate that selecting the "Cancel" button on the dialog will cause the view model to set the exit details
        // live data.  This will cause the details fragment to exit.
        viewModel.processDialogSelections(R.string.no_internet_title, false)
        TimeUnit.MILLISECONDS.sleep(5000)
        assertEquals(viewModel.exitDetails.value, true)

        // Validate that selecting the "Check Internet" button on the dialog will cause the view model to set the exit details
        // live data.  This will cause the details fragment to exit.
        viewModel.processDialogSelections(R.string.no_internet_title, true)
        TimeUnit.MILLISECONDS.sleep(5000)
        assertEquals(viewModel.openSettings.value, true)
    }

}