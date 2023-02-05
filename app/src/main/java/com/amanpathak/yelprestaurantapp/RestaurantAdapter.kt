package com.amanpathak.yelprestaurantapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.amanpathak.yelprestaurantapp.databinding.ListItemRestaurantlBinding
import com.amanpathak.yelprestaurantapp.helper.Utils
import com.amanpathak.yelprestaurantapp.models.Restaurant
import com.bumptech.glide.Glide


class RestaurantAdapter(val utils: Utils, val context: Fragment, private val list: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(val item: ListItemRestaurantlBinding?) : RecyclerView.ViewHolder(item!!.root) {

        fun bind(data : Any){
             val restaurant = data as Restaurant
             item?.name?.text = restaurant.name
             item?.desc?.text = "${utils.convertDistanceToReadableForm(restaurant.distanceInMeter)} | ${restaurant.address}"
             Glide.with(context).load(restaurant.imageUrl).into(item?.restroImage!!)

            restaurant.isClosed?.let {
                item.status.text = if(it) "Closed" else "Open"
                item.status.background = if(it) AppCompatResources.getDrawable(context.requireContext(),R.drawable.closed_status_background) else AppCompatResources.getDrawable(context.requireContext(), R.drawable.open_status_background)
            }

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

            if(holder.adapterPosition == list.size - 4){
                (context as RestaurantAdapterInteraction).onPagination()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    interface RestaurantAdapterInteraction{
        fun onPagination()
    }



}