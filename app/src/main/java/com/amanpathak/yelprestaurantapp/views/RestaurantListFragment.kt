package com.amanpathak.yelprestaurantapp.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.lang.Exception

class RestaurantListFragment : Fragment() {
    lateinit var binding : FragmentRestaurantListBinding
    val viewModel : MainViewModel by activityViewModels()

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
        viewModel.currentLocation.observe(viewLifecycleOwner, Observer { location ->
            if(location != null){
                viewModel.getRestaurantAsPerLocation()
            }
        })

        viewModel.restaurantList.observe(viewLifecycleOwner, Observer {

            try {
                binding.restaurantRecyclerView.adapter =
                    RestaurantAdapter(utils = Utils(), requireContext(), it)
            }
            catch (e : Exception){

            }
        })

    }

    private fun initViews() {
        binding.restaurantRecyclerView.layoutManager = LinearLayoutManager(context,
            RecyclerView.VERTICAL, false)

    }
}