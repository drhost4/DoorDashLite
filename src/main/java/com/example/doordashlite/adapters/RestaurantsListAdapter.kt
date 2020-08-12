package com.example.doordashlite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doordashlite.R
import com.example.doordashlite.models.Restaurant
import com.example.doordashlite.viewmodels.RestaurantsViewModel

class RestaurantsListAdapter(
    private val restaurants: List<Restaurant>,
    private val viewModel: RestaurantsViewModel
) :
    RecyclerView.Adapter<RestaurantsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.restaurant_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurants[position]
        Glide.with(holder.image.context).load(restaurant.coverImageUrl).into(holder.image)
        holder.name.text = restaurant.name
        holder.type.text = restaurant.description
        holder.deliveryTime.text = restaurant.status
        holder.mainLayout.setOnClickListener { _ -> viewModel.processSelection(position) }
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mainLayout: ConstraintLayout
        var image: ImageView
        var name: TextView
        var type: TextView
        var deliveryTime: TextView

        init {
            with(itemView) {
                mainLayout = findViewById(R.id.restaurantItemLayout)
                image = findViewById(R.id.restaurantLogo)
                name = findViewById(R.id.restaurantName)
                type = findViewById(R.id.restaurantType)
                deliveryTime = findViewById(R.id.restaurantDeliveryTime)
            }
        }


    }
}