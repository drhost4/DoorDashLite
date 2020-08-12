package com.example.doordashlite

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.doordashlite.fragments.RestaurantDetailFragment
import com.example.doordashlite.fragments.RestaurantsFragment
import com.example.doordashlite.viewmodels.RestaurantsViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val colorDrawable = ColorDrawable(Color.parseColor("#FF0000"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProviders.of(this).get(RestaurantsViewModel::class.java)
        viewModel.displayDetails.observe(this, Observer<Boolean> { _ -> displayDetails() })
        viewModel.exitApp.observe(this, Observer<Boolean> {
            finish()
        })

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.mainFrameLayout,
            RestaurantsFragment(), "restaurantList"
        )
        transaction.commit()
    }

    private fun displayDetails() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_from_bottom,
            R.anim.slide_to_bottom,
            R.anim.slide_from_bottom,
            R.anim.slide_to_bottom
        )
        transaction.add(
            R.id.mainFrameLayout,
            RestaurantDetailFragment(), "restaurantDetails"
        )
        transaction.addToBackStack("restaurantDetails")
        transaction.commit()
    }
}