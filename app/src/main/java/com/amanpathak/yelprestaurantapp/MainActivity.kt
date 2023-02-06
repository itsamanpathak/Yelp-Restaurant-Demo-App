package com.amanpathak.yelprestaurantapp

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import com.amanpathak.yelprestaurantapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listener()
    }

    private fun listener() {

        viewModel.event.observe(this, Observer {
            when (it) {

                is MainViewModel.Event.RequestPermission -> {
                    viewModel.permissionResultLauncher.launch(it.permissions.toTypedArray())
                }
            }
        })

        viewModel.permissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            it.forEach { permission ->
                if (permission.value) {
                    viewModel.fetchCurrentLocation()
                } else {
                    val shouldShowRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(this, permission.key)
                    if (shouldShowRationale) {
                        showAlertDialog(
                            getString(R.string.location_permission_message),
                            getString(R.string.ok_text),
                            getString(R.string.deny_text),
                        ) {
                            viewModel.fetchCurrentLocation()
                        }
                    } else {

                        showAlertDialog(
                            getString(R.string.location_permission_message),
                            getString(R.string.ok_text),
                            getString(R.string.deny_text),
                        ) {
                            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", this@MainActivity.packageName, null)
                            })
                        }
                    }
                }
            }
        }

    }

    private fun showAlertDialog(
        message: String, positiveButtonText: String,
        negativeButtonText: String, positiveButtonFn: () -> Unit
    ) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(
                positiveButtonText
            ) { _, i -> positiveButtonFn() }
            .setNegativeButton(
                negativeButtonText
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .create().show()
    }


}