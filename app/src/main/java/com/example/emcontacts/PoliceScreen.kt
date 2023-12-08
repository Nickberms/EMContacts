package com.example.emcontacts

import android.annotation.SuppressLint
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import android.content.Context
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
import com.example.emcontacts.interfaces.Municipality
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
    val mapsUrl = "https://www.google.com/maps?q=$latitude,$longitude"
    SmsUtils.sendSMS(context, phoneNumber, mapsUrl)
}

data class PoliceAdmin(
    val documentId: String,
    val contactNumber: String,
)

class PoliceViewModel : ViewModel() {
    private val _police = MutableLiveData<List<PoliceAdmin>>()
    val policeLive: LiveData<List<PoliceAdmin>> = _police
    private val db = Firebase.firestore
    fun getEmergencyPhoneNumber(finalMunicipalityForm: String) {
        db.collection("Emergency Contacts")
            .document(finalMunicipalityForm)
            .collection("Police")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val policeLiveData = mutableListOf<PoliceAdmin>()
                snapshot?.documents?.forEach { document ->
                    val documentId = document.id
                    val contactNumber = document.getString("contacts") ?: ""
                    policeLiveData.add(PoliceAdmin(documentId, contactNumber))
                }
                _police.postValue(policeLiveData)
            }
    }
}

@SuppressLint(
    "UnrememberedMutableState", "SuspiciousIndentation", "StateFlowValueCalledInComposition"
)
@Composable
fun PoliceScreen() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val isPreview = LocalInspectionMode.current
    Color(0xFFFFD317)
    var selectedMunicipality by remember { mutableStateOf<Municipality?>(null) }
    val context = LocalContext.current
    remember { mutableStateOf(listOf<Municipality>()) }
    rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("municipality_prefs", Context.MODE_PRIVATE)
    val defaultMunicipalityName = sharedPreferences.getString("selected_municipality", "Valencia")
    remember { mutableStateOf<String?>(null) }
    rememberNavController()
    val dialerLauncher: ActivityResultLauncher<Intent> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
    }
    val locationHelper = LocationUtils(context = LocalContext.current)
    var userLocation by remember { mutableStateOf<Location?>(null) }
    val viewModel = PoliceViewModel()
    if (defaultMunicipalityName != null) {
        viewModel.getEmergencyPhoneNumber(defaultMunicipalityName)
    }
    LaunchedEffect(true) {
        locationHelper.getDeviceLocation { location ->
            userLocation = location
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            locationHelper.stopLocationUpdates()
        }
    }
    val mutableNewContactNumber = MutableStateFlow("")
    hashMapOf("contacts" to mutableNewContactNumber.value)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerComponent(drawerState) { municipality ->
                selectedMunicipality = municipality
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
                    selectedMunicipality?.let {
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
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material.Text(
                        text = "POLICE",
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                val fireListView by viewModel.policeLive.observeAsState(initial = emptyList())
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(1.0f)

                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        item {
                            if (fireListView.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                for (Police in fireListView) {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Column (
                                        modifier = Modifier.background(Color(0xFFFFD317))
                                            .padding(horizontal = 16.dp)
                                            .padding(top = 5.dp)
                                            .padding(bottom = 8.dp),
                                    )  {
                                        Text(
                                            text = Police.documentId,
                                            color = Color.Black,
                                            style = androidx.compose.ui.text.TextStyle(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            ),
                                            modifier = Modifier
                                                .padding(
                                                    4.dp,
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
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Text(
                                                    text = Police.contactNumber,
                                                    color = Color.Black,
                                                    style = androidx.compose.ui.text.TextStyle(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp
                                                    ),
                                                    modifier = Modifier.padding(start = 10.dp)
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
                                                                sendLocationSMS(
                                                                    context = context,
                                                                    location = location,
                                                                    phoneNumber = Police.contactNumber
                                                                )
                                                            } ?: run {
                                                            }
                                                        }
                                                )
                                                Spacer(modifier = Modifier.width(25.dp))
                                                val contactNumber = Police.contactNumber
                                                Icon(
                                                    imageVector = Icons.Default.Phone,
                                                    contentDescription = null,
                                                    tint = Color.Black,
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clickable {
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
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}