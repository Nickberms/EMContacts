package com.example.emcontacts.admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.example.emcontacts.interfaces.DrawerComponent
import com.example.emcontacts.interfaces.Municipality
import com.example.emcontacts.utils.SmsUtils
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.unit.sp
import com.example.emcontacts.interfaces.LocationMap
import com.example.emcontacts.utils.LocationUtils

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun RescuersHeaderComponent(finalMunicipalityForm: String) {
    val showDialog = remember { mutableStateOf(false) }
    val createNewDocument = remember { mutableStateOf("") }
    val createNewNumber = remember { mutableStateOf("") }
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFFFD317)
        ),
        navigationIcon = {
        },
        title = {
            Text(
                text = "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text(
                    color = Color(0xFF000000),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.padding(end = 4.dp),
                    overflow = TextOverflow.Ellipsis,
                    text = "Add a contact number"
                )
                IconButton(onClick = {
                    createNewDocument.value = ""
                    createNewNumber.value = ""
                    showDialog.value = true
                }) {
                    Icon(
                        contentDescription = "Add",
                        imageVector = Icons.Filled.Add,
                        modifier = Modifier.size(38.dp),
                        tint = Color(0xFF000000)
                    )
                }
            }
        }
    )
    if (showDialog.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = createNewDocument.value,
                        onValueChange = { createNewDocument.value = it },
                        label = { Text("Contact Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = createNewNumber.value,
                        onValueChange = { newValue ->
                            if (newValue.length <= 11) {
                                createNewNumber.value = newValue
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        label = { Text("Contact Number") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (createNewDocument.value.isNotEmpty()) {
                            val db = Firebase.firestore
                            db.collection("Emergency Contacts")
                                .document(finalMunicipalityForm)
                                .collection("Rescuers")
                                .document(createNewDocument.value)
                                .set(hashMapOf("number" to createNewNumber.value))
                            showDialog.value = false
                        }
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog.value = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@SuppressLint(
    "UnrememberedMutableState", "SuspiciousIndentation",
    "StateFlowValueCalledInComposition"
)
@Composable
fun AdminRescuersScreen() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    LocalInspectionMode.current
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
    val viewModel = RescueViewModel()
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
    hashMapOf("number" to mutableNewContactNumber.value)
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
                if (defaultMunicipalityName != null) {
                    RescuersHeaderComponent(defaultMunicipalityName)
                }
                Spacer(modifier = Modifier.height(16.dp))
                selectedMunicipality?.let {
                    LocationMap(
                        modifier = Modifier
                            .fillMaxHeight(0.3f),
                        onLocationUpdate = {
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "RESCUERS",
                        color = Color(0xFFFFFFFF),
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                val rescuerList by viewModel.rescuersList.observeAsState(initial = emptyList())
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
                            if (rescuerList.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                for (rescuer in rescuerList) {
                                    rescuer.documentId
                                    val contactNumber = rescuer.contactNumber
                                    var isEditing by remember { mutableStateOf(false) }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Column(
                                        modifier = Modifier
                                            .background(Color(0xFFFFD317))
                                            .padding(horizontal = 16.dp)
                                            .padding(top = 5.dp)
                                            .padding(bottom = 8.dp),
                                    ) {
                                        Text(
                                            text = rescuer.documentId,
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
                                            remember { mutableStateOf(contactNumber) }
                                            val showDialog = remember { mutableStateOf(false) }
                                            val textFieldIdValue = remember { mutableStateOf("") }
                                            val textFieldContactValue =
                                                remember { mutableStateOf("") }
                                            Box(
                                                modifier = Modifier
                                                    .size(width = 165.dp, height = 40.dp)
                                                    .border(2.dp, Color.Black)
                                                    .background(Color.White),
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = null,
                                                    tint = Color.Black,
                                                    modifier = Modifier
                                                        .align(Alignment.CenterEnd)
                                                        .size(28.dp)
                                                        .padding(end = 4.dp)
                                                        .clickable {
                                                            isEditing = !isEditing
                                                            showDialog.value = true
                                                        }
                                                )
                                                if (showDialog.value) {
                                                    textFieldIdValue.value = rescuer.documentId
                                                    textFieldContactValue.value =
                                                        rescuer.contactNumber
                                                    AlertDialog(
                                                        onDismissRequest = {
                                                            showDialog.value = false
                                                        },
                                                        text = {
                                                            Column {
                                                                OutlinedTextField(
                                                                    value = textFieldIdValue.value,
                                                                    onValueChange = {
                                                                        textFieldIdValue.value = it
                                                                    },
                                                                    label = { Text("Contact Name") }
                                                                )
                                                                OutlinedTextField(
                                                                    value = textFieldContactValue.value,
                                                                    onValueChange = { newValue ->
                                                                        if (newValue.length <= 11) {
                                                                            textFieldContactValue.value =
                                                                                newValue
                                                                        }
                                                                    },
                                                                    keyboardOptions = KeyboardOptions.Default.copy(
                                                                        keyboardType = KeyboardType.Number
                                                                    ),
                                                                    label = { Text("Contact Number") }
                                                                )
                                                            }
                                                        },
                                                        confirmButton = {
                                                            Button(
                                                                onClick = {
                                                                    if (rescuer.documentId != textFieldIdValue.value || rescuer.contactNumber != textFieldContactValue.value) {
                                                                        val db = Firebase.firestore
                                                                        val docRef =
                                                                            db.collection("Emergency Contacts")
                                                                                .document("$defaultMunicipalityName")
                                                                                .collection("Rescuers")
                                                                                .document(
                                                                                    rescuer.documentId
                                                                                )
                                                                        docRef.get()
                                                                            .addOnSuccessListener { snapshot ->
                                                                                if (snapshot.exists()) {
                                                                                    if (rescuer.documentId != textFieldIdValue.value) {
                                                                                        db
                                                                                            .collection(
                                                                                                "Emergency Contacts"
                                                                                            )
                                                                                            .document(
                                                                                                "$defaultMunicipalityName"
                                                                                            )
                                                                                            .collection(
                                                                                                "Rescuers"
                                                                                            )
                                                                                            .document(
                                                                                                rescuer.documentId
                                                                                            )
                                                                                            .delete()
                                                                                            .addOnSuccessListener {
                                                                                                val newDocumentId =
                                                                                                    textFieldIdValue.value
                                                                                                var contactNumber =
                                                                                                    textFieldContactValue.value
                                                                                                if (contactNumber.isEmpty()) {
                                                                                                    contactNumber =
                                                                                                        rescuer.contactNumber
                                                                                                }
                                                                                                db.collection(
                                                                                                    "Emergency Contacts"
                                                                                                )
                                                                                                    .document(
                                                                                                        "$defaultMunicipalityName"
                                                                                                    )
                                                                                                    .collection(
                                                                                                        "Rescuers"
                                                                                                    )
                                                                                                    .document(
                                                                                                        newDocumentId
                                                                                                    )
                                                                                                    .set(
                                                                                                        hashMapOf(
                                                                                                            "number" to contactNumber,
                                                                                                        )
                                                                                                    )
                                                                                                    .addOnSuccessListener {
                                                                                                        textFieldIdValue.value =
                                                                                                            ""
                                                                                                        textFieldContactValue.value =
                                                                                                            ""
                                                                                                        showDialog.value =
                                                                                                            false
                                                                                                    }
                                                                                            }
                                                                                    } else {
                                                                                        db.collection(
                                                                                            "Emergency Contacts"
                                                                                        )
                                                                                            .document(
                                                                                                "$defaultMunicipalityName"
                                                                                            )
                                                                                            .collection(
                                                                                                "Rescuers"
                                                                                            )
                                                                                            .document(
                                                                                                rescuer.documentId
                                                                                            )
                                                                                            .update(
                                                                                                "number",
                                                                                                textFieldContactValue.value
                                                                                            )
                                                                                            .addOnSuccessListener {
                                                                                                showDialog.value =
                                                                                                    false
                                                                                                textFieldIdValue.value =
                                                                                                    ""
                                                                                                textFieldContactValue.value =
                                                                                                    ""
                                                                                            }
                                                                                    }
                                                                                }
                                                                            }
                                                                    }
                                                                }
                                                            ) {
                                                                Text("Submit")
                                                            }
                                                        },
                                                        dismissButton = {
                                                            Button(onClick = {
                                                                showDialog.value = false
                                                            }) {
                                                                Text("Cancel")
                                                            }
                                                        }
                                                    )
                                                } else {
                                                    androidx.compose.material3.Text(
                                                        text = rescuer.contactNumber,
                                                        color = Color.Black,
                                                        style = androidx.compose.ui.text.TextStyle(
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 15.sp
                                                        ),
                                                        modifier = Modifier.padding(start = 10.dp)
                                                    )
                                                }
                                            }
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clickable {
                                                        if (defaultMunicipalityName != null) {
                                                            val db = Firebase.firestore
                                                            db
                                                                .collection("Emergency Contacts")
                                                                .document("$defaultMunicipalityName")
                                                                .collection("Rescuers")
                                                                .document(rescuer.documentId)
                                                                .delete()
                                                                .addOnSuccessListener {
                                                                    isEditing = false
                                                                }
                                                        }
                                                    }
                                            )
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
                                                                phoneNumber = contactNumber
                                                            )
                                                        } ?: run {
                                                        }
                                                    }
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
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
                                    Spacer(
                                        modifier = Modifier
                                            .height(1.dp)
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

private fun sendLocationSMS(context: Context, location: Location, phoneNumber: String) {
    val latitude = location.latitude
    val longitude = location.longitude
    val mapsUrl = "https://www.google.com/maps?q=$latitude,$longitude"
    SmsUtils.sendSMS(context, phoneNumber, mapsUrl)
}

data class RescuersAdmin(
    val documentId: String,
    val contactNumber: String,
)

class RescueViewModel : ViewModel() {
    private val _rescue = MutableLiveData<List<RescuersAdmin>>()
    val rescuersList: LiveData<List<RescuersAdmin>> = _rescue
    private val db = Firebase.firestore
    fun getEmergencyPhoneNumber(finalMunicipalityForm: String) {
        db.collection("Emergency Contacts")
            .document(finalMunicipalityForm)
            .collection("Rescuers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val rescuersList = mutableListOf<RescuersAdmin>()
                snapshot?.documents?.forEach { document ->
                    val documentId = document.id
                    val contactNumber = document.getString("number") ?: ""
                    rescuersList.add(RescuersAdmin(documentId, contactNumber))
                }
                _rescue.postValue(rescuersList)
            }
    }
}