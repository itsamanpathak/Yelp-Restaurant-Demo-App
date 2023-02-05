package com.amanpathak.yelprestaurantapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.app.NavUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amanpathak.yelprestaurantapp.MainViewModel
import com.amanpathak.yelprestaurantapp.RestaurantAdapter
import com.amanpathak.yelprestaurantapp.databinding.FragmentRestaurantListBinding
import com.amanpathak.yelprestaurantapp.helper.Utils
import com.amanpathak.yelprestaurantapp.models.Restaurant
import java.lang.Exception

class RestaurantListFragment : Fragment(), RestaurantAdapter.RestaurantAdapterInteraction {
    lateinit var binding : FragmentRestaurantListBinding
    val viewModel : MainViewModel by activityViewModels()
    lateinit var adapter : RestaurantAdapter
    val list = ArrayList<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantListBinding.inflate(layoutInflater, null, false)
        initViews()
        listeners()
        return binding.root
    }

    private fun listeners() {

        binding.swiperefresh.setOnRefreshListener {
            viewModel.onRefresh()
        }

        viewModel.currentLocation.observe(viewLifecycleOwner, Observer { location ->
            if(location != null){
                viewModel.getRestaurantAsPerLocation()
            }
        })

        binding.radiusSeekBar.progress = getSeekBarProgress(viewModel.radiusInMeters.value!!)

        viewModel.radiusInMeters.observe(viewLifecycleOwner, Observer {
            binding.distance.text = Utils.convertDistanceToReadableForm(it.toDouble())
        })

        viewModel.restaurantList.observe(viewLifecycleOwner, Observer {

            binding.fetchingLocation.isVisible = false
            binding.swiperefresh.isRefreshing = false

            if(!viewModel.isPagination){
                list.clear()
                list.addAll(it)

                try {
                    adapter = RestaurantAdapter(utils = Utils, this, list)
                    binding.restaurantRecyclerView.adapter = adapter
                }
                catch (e : Exception){

                }
            }
            else
            {
                val size = list.size
                list.addAll(it)
                adapter.notifyItemRangeChanged(size, it.size)

            }
        })

        viewModel.showPaginationProgress.observe(viewLifecycleOwner, Observer {
            binding.paginationProgress.isVisible = it
        })


        binding.radiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            var isFromUser = false


            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                isFromUser = fromUser
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if(isFromUser && seekBar != null){
                    viewModel.onRadiusSeekBarChange(seekBar.progress)
                }
            }

        })

    }

    private fun getSeekBarProgress(currentRadius : Int) : Int{
        return ((currentRadius / 5000.toFloat()) * 100).toInt()
    }



    private fun initViews() {
        binding.fetchingLocation.isVisible = true
        binding.restaurantRecyclerView.layoutManager = LinearLayoutManager(context,
            RecyclerView.VERTICAL, false)

    }

    override fun onPagination() {
        viewModel.onPagination()
    }
}