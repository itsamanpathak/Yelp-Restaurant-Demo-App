package com.amanpathak.yelprestaurantapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amanpathak.yelprestaurantapp.databinding.ListItemRestaurantlBinding
import com.amanpathak.yelprestaurantapp.helper.Utils
import com.amanpathak.yelprestaurantapp.models.Restaurant
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class RestaurantAdapter(val utils: Utils, val context: Context, private val list: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(val item: ListItemRestaurantlBinding?) : RecyclerView.ViewHolder(item!!.root) {

        fun bind(data : Any){
             val restaurant = data as Restaurant
             item?.name?.text = restaurant.name
             item?.desc?.text = "${restaurant.distanceInMeter} ${restaurant.address}"
             Glide.with(context).load(restaurant.imageUrl).into(item?.restroImage!!)
             item.status.text = if(restaurant.isClosed == true) "Closed" else "Open"
             item.rating.text = "${restaurant.rating}"
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemRestaurantlBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = list[position]
        if(holder is ViewHolder){
            holder.bind(data)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}