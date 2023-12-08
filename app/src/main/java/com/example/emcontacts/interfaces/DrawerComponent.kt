package com.example.emcontacts.interfaces

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun DrawerItem(text: String, onClick: () -> Unit = {}) {
    Text(
        color = Color(0xFF000000),
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
        text = text
    )
}

data class Municipality(
    val name: String,
    val geolocation: GeoPoint
)

@Composable
fun DrawerComponent(drawerState: DrawerState, onMunicipalitySelected: (Municipality) -> Unit) {
    val context = LocalContext.current
    val municipalities = remember { mutableStateOf(listOf<Municipality>()) }
    val scope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("municipality_prefs", Context.MODE_PRIVATE)
    val defaultMunicipalityName = sharedPreferences.getString("selected_municipality", "Valencia")
    LaunchedEffect(Unit) {
        scope.launch {
            val db = Firebase.firestore
            val result = mutableListOf<Municipality>()
            db.collection("Emergency Contacts").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val geolocation = document.getGeoPoint("Geolocation")
                        if (geolocation != null) {
                            val municipality = Municipality(document.id, geolocation)
                            result.add(municipality)
                        }
                    }
                    municipalities.value = result
                    val defaultMunicipality =
                        municipalities.value.find { it.name == defaultMunicipalityName }
                    if (defaultMunicipality != null) {
                        onMunicipalitySelected(defaultMunicipality)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("DrawerComponent", "Error getting documents: ", exception)
                }
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .background(Color(0xFFFFFFFF))
                    .width(LocalConfiguration.current.screenWidthDp.dp / 2)
            ) {
                Column {
                    DrawerItem(text = "Municipality")
                    Divider()
                    for (municipality in municipalities.value) {
                        DrawerItem(
                            onClick = {
                                onMunicipalitySelected(municipality)
                                with(sharedPreferences.edit()) {
                                    putString("selected_municipality", municipality.name)
                                    apply()
                                }
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            text = municipality.name
                        )
                    }
                }
            }
        },
        content = {
        }
    )
}