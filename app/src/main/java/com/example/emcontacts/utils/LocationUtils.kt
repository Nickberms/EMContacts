package com.example.emcontacts.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LocationUtils(private val context: Context) : ViewModel() {
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private val REQUEST_LOCATION_PERMISSION = 1

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(onLocationUpdate: (Location) -> Unit) {
        if (locationManager == null) {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        if (checkPermissions()) {
            locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    viewModelScope.launch {
                        onLocationUpdate(location)
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                }

                override fun onProviderEnabled(provider: String) {
                }

                override fun onProviderDisabled(provider: String) {
                }
            }
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0f,
                locationListener!!
            )
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    fun stopLocationUpdates() {
        locationListener?.let { locationManager?.removeUpdates(it) }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}