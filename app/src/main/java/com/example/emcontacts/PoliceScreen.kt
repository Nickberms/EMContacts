package com.example.emcontacts

import android.annotation.SuppressLint
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.emcontacts.interfaces.Municipality
import com.google.firebase.database.*
import kotlinx.coroutines.*
import com.example.emcontacts.interfaces.DrawerComponent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.runtime.DisposableEffect
import com.example.emcontacts.utils.SmsUtils
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.example.emcontacts.interfaces.ContactsHeader
import com.example.emcontacts.interfaces.LocationMap
import com.example.emcontacts.utils.LocationUtils
import kotlinx.coroutines.flow.MutableStateFlow


private fun sendLocationSMS(context: Context, location: Location, phoneNumber: String) {
    val latitude = location.latitude
    val longitude = location.longitude

    // Create a Google Maps URL with the latitude and longitude
    val mapsUrl = "https://www.google.com/maps?q=$latitude,$longitude"

    // Create the SMS message with a hyperlink
    val smsMessage = "Help! I'm at Latitude: $latitude, Longitude: $longitude. Open in Google Maps: $mapsUrl"

    // Send SMS with the message
    SmsUtils.sendSMS(context, phoneNumber, smsMessage)
}


data class PoliceAdmin(
    val documentId: String,
    val contactNumber: String,
)

class PoliceViewModel : ViewModel() {

    private val _police = MutableLiveData<List<PoliceAdmin>>()
    private val PoliceLiveData = MutableLiveData<List<PoliceAdmin>>()
    val PoliceLiveDataModel: LiveData<List<PoliceAdmin>> = _police

    private val db = Firebase.firestore
    private val TAG = "FireStore Data Retrieval"

    fun getEmergencyPhoneNumber(finalMunicipalityForm: String) {
        Log.d(TAG, "Retrieving emergency phone numbers for municipality: $finalMunicipalityForm")

        db.collection("Emergency Contacts")
            .document("$finalMunicipalityForm")
            .collection("Police")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error retrieving PoliceLiveDataModel: ${error.message}", error)
                    return@addSnapshotListener
                }

                val PoliceLiveDataModel = mutableListOf<PoliceAdmin>()
                snapshot?.documents?.forEach { document ->
                    val documentId = document.id
                    val contactNumber = document.getString("contacts") ?: ""
                    PoliceLiveDataModel.add(PoliceAdmin(documentId, contactNumber))

                    Log.d(TAG, "Retrieved Police: $documentId -> $contactNumber")
                }

                //    PoliceLiveData.setValue(PoliceLiveDataModel)
                _police.postValue(PoliceLiveDataModel)
            }
    }

}



@SuppressLint("UnrememberedMutableState", "SuspiciousIndentation", "StateFlowValueCalledInComposition"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliceScreen(navController: NavController?, selectedMunicipality: String?) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val isPreview = LocalInspectionMode.current
    val containerColor = Color(0xFFFFD317)
    var selectedMunicipality by remember { mutableStateOf<Municipality?>(null) }

    val context = LocalContext.current
    val municipalities = remember { mutableStateOf(listOf<Municipality>()) }
    val scope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("municipality_prefs", Context.MODE_PRIVATE)
    val defaultMunicipalityName = sharedPreferences.getString("selected_municipality", "Valencia")
    val finalMunicipalityForm = defaultMunicipalityName
    var errorState by remember { mutableStateOf<String?>(null) }
    // Call getEmergencyPhoneNumber within a composable function
    val emergencyPhoneNumber = remember { mutableStateOf<String?>(null) }

    Log.d("MedicPage", "Selected Municipality: $finalMunicipalityForm")

    val navController = rememberNavController()

    var selectedPhoneNumber by remember { mutableStateOf<String?>(null) }

    val dialerLauncher: ActivityResultLauncher<Intent> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result if needed
    }

    // Create an instance of LocationHelper within the composable function
    val locationHelper = LocationUtils(context = LocalContext.current)
    // State variable to hold the user's location
    var userLocation by remember { mutableStateOf<Location?>(null) }

    //  var PoliceLiveDataModel = finalMunicipalityForm?.let { getEmergencyPhoneNumber(it) }


    val viewModel = PoliceViewModel()
    if (finalMunicipalityForm != null) {
        viewModel.getEmergencyPhoneNumber(finalMunicipalityForm)
    }

    // Call getDeviceLocation within a composable function
    LaunchedEffect(true) {
        locationHelper.getDeviceLocation { location ->
            userLocation = location
            Log.d(
                "Location_firePage",
                "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
            )
        }
    }

    DisposableEffect(Unit) {
        // Clean up when the composable is disposed
        onDispose {
            locationHelper.stopLocationUpdates()
        }
    }

    val mutableNewContactNumber = MutableStateFlow("")

    val newmedicDocument = hashMapOf("contacts" to mutableNewContactNumber.value)


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerComponent(drawerState) { municipality ->
                selectedMunicipality = municipality
                Log.d("LOCATION", "HomeScreen: $municipality")
            }
        }
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFF1E2128))
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {

                ContactsHeader()

                Spacer(modifier = Modifier.height(16.dp))

                if (isPreview) {
                    // Placeholder for the map in preview mode
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(Color(0xFFFFFFFF))
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        androidx.compose.material.Text(
                            "Map Placeholder",
                            textAlign = TextAlign.Center
                        )
                    }
                } else {

                    selectedMunicipality?.let { municipality ->
                        LocationMap(
                            modifier = Modifier
                                .fillMaxHeight(0.3f),
                            onLocationUpdate = {
                            }
                        )
                    }

                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), // Add padding as needed
                    horizontalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material.Text(
                        text = "Police Contact Page",
                        color = Color.White,
                        style = LocalTextStyle.current,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                        // Adjust the horizontal padding as needed
                    )
                }


                Spacer(modifier = Modifier.height(8.dp))

                val fireListView by viewModel.PoliceLiveDataModel.observeAsState(initial = emptyList())

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.6f)
                        .background(containerColor) // Set the background color as needed
                ) {
                    // Display the data fetched from Firestore

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        item {

                            if (fireListView.isEmpty()) {
                                // Show placeholder text or view indicating no data
                                Log.d("ViewModel", "AdminMedicPage: Empty")
                                // Show loading indicator
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }

                            } else {
                                for (Police in fireListView) {

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Column {

                                        Text(
                                            text = Police.documentId,
                                            color = Color.Black,
                                            style = LocalTextStyle.current,
                                            modifier = Modifier
                                                .paddingFromBaseline(
                                                    10.dp,
                                                    0.dp
                                                )
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(5.dp),
                                        ) {
                                            Box(

                                                modifier = Modifier
                                                    .size(width = 165.dp, height = 40.dp)
                                                    .border(2.dp, Color.Black)
                                                    .background(Color.White),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = Police.contactNumber,
                                                    color = Color.Black,
                                                    style = LocalTextStyle.current
                                                )
                                            }


                                            Row {

                                                Icon(
                                                    imageVector = Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = Color.Black,
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clickable {
                                                            userLocation?.let { location ->
                                                                // Call a function to handle sending SMS with location and phone number
                                                                sendLocationSMS(
                                                                    context = context,
                                                                    location = location,
                                                                    phoneNumber = Police.contactNumber
                                                                )
                                                            } ?: run {
                                                                Log.w(
                                                                    "Clickable",
                                                                    "User location is null"
                                                                )
                                                            }
                                                        }
                                                )

                                                Spacer(modifier = Modifier.width(25.dp))

                                                var contactNumber =  Police.contactNumber
                                                Icon(
                                                    imageVector = Icons.Default.Phone,
                                                    contentDescription = null,
                                                    tint = Color.Black,
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clickable {
                                                            // Launch the dialer here
                                                            val dialIntent = Intent(
                                                                Intent.ACTION_DIAL,
                                                                Uri.parse("tel:$contactNumber")
                                                            )
                                                            dialerLauncher.launch(dialIntent)
                                                        }
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))

                                    Spacer(
                                        modifier = Modifier
                                            .height(2.dp)
                                            .fillMaxWidth()
                                            .background(Color.Black)
                                    )

                                    Spacer(modifier = Modifier.height(5.dp))


                                }
                            }
                        }
                    }
                }
            }
        }
    }


}