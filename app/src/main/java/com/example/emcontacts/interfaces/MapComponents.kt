package com.example.emcontacts.interfaces

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.emcontacts.utils.LocationUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun MunicipalityMap(
    context: Context = LocalContext.current,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    municipality: Municipality,
    zoomLevel: Float = 14f
) {
    val mapView = remember {
        MapView(context).apply {
            onCreate(null)
            onResume()
        }
    }
    AndroidView({ mapView }, modifier = modifier) { mv ->
        mv.getMapAsync { googleMap ->
            val latLng =
                LatLng(municipality.geolocation.latitude, municipality.geolocation.longitude)
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(municipality.name)
            )?.showInfoWindow()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
        }
    }
}

@Composable
fun LocationMap(
    context: Context = LocalContext.current,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onLocationUpdate: (Location) -> Unit
) {
    var location by remember { mutableStateOf<Location?>(null) }
    val mapView = remember {
        MapView(context).apply {
            onCreate(null)
            onResume()
        }
    }
    val locationHelper by remember { mutableStateOf(LocationUtils(context)) }
    DisposableEffect(context) {
        locationHelper.getDeviceLocation {
            location = it
            onLocationUpdate(it)
        }
        onDispose {
            locationHelper.stopLocationUpdates()
        }
    }
    AndroidView({ mapView }, modifier = modifier) { mv ->
        location?.let { currentLocation ->
            mv.getMapAsync { googleMap ->
                val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Current Location")
                )?.showInfoWindow()
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
            }
        }
    }
}