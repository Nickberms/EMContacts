package com.example.emcontacts.admin


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
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
import com.example.emcontacts.interfaces.LocationMap
import com.example.emcontacts.utils.LocationUtils


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun FirefightersHeaderComponent(navController: NavController?, finalMunicipalityForm: String) {
    val showDialog = remember { mutableStateOf(false) }
    val CreatedocumentNewId = remember { mutableStateOf("") }
    val CreatecontactNewId = remember { mutableStateOf("") }

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
            // Add the plus icon
            IconButton(
                onClick = {
                    showDialog.value = true
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(end = 10.dp),
                //    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add a contact number",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(20.dp)) // Add space between text and icon
                    Icon(
                        contentDescription = "ADD",
                        imageVector = Icons.Filled.Add,
                        modifier = Modifier.size(32.dp),
                        tint = Color(0xFF000000)
                    )
                }

            }

        }
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = { Text("Update Document Data") },
            text = {
                Column {
                    OutlinedTextField(
                        value = CreatedocumentNewId.value,
                        onValueChange = { CreatedocumentNewId.value = it },
                        label = { Text("New Document ID") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = CreatecontactNewId.value,
                        onValueChange = { newValue ->
                            // Limit the length to 11 digits
                            if (newValue.length <= 11) {
                                CreatecontactNewId.value = newValue
                            }

                            // Validate the input
                            if (newValue.isNotEmpty() && newValue.all { char -> char.isDigit() }) {
                                if (newValue.length == 11) {
                                    // Valid contact number
                                } else {
                                    Log.e(
                                        "ContactNumber",
                                        "Invalid contact number. Please enter a 11-digit number."
                                    )
                                }
                            } else {
                                Log.e("ContactNumber", "Please enter only digits.")
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        label = { Text("Input Contact Number") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Simulate document update logic
                        if (CreatedocumentNewId.value.isNotEmpty()) {
                            var db = Firebase.firestore
                            db.collection("Emergency Contacts")
                                .document("$finalMunicipalityForm")
                                .collection("Firefighters")
                                .document(CreatedocumentNewId.value)
                                .set(hashMapOf("contacts" to CreatecontactNewId.value,))
                                .addOnSuccessListener {
                                    Log.d("InsertDocument", "New document inserted successfully")
                                    showDialog.value = false
                                    CreatedocumentNewId.value = ""
                                    CreatecontactNewId.value = ""
                                    //       Toast.makeText(context, "Document updated successfully", Toast.LENGTH_SHORT).show()

                                }
                                .addOnFailureListener { e ->
                                    Log.e(
                                        "InsertDocument",
                                        "Error inserting document",
                                        e
                                    )
                                }

                            Log.d("Document Update", "Updating document with new information...")
                            showDialog.value = false
                            //    Toast.makeText(context, "Document updated successfully", Toast.LENGTH_SHORT).show()
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


data class FirefighterAdmin(
    val documentId: String,
    val contactNumber: String,
)

class EmergencyViewModel : ViewModel() {

    private val _firefighters = MutableLiveData<List<FirefighterAdmin>>()
    private val firefightersLiveData = MutableLiveData<List<FirefighterAdmin>>()
    val firefighters: LiveData<List<FirefighterAdmin>> = _firefighters

    private val db = Firebase.firestore
    private val TAG = "FireStore Data Retrieval"

    fun getEmergencyPhoneNumber(finalMunicipalityForm: String) {
        Log.d(TAG, "Retrieving emergency phone numbers for municipality: $finalMunicipalityForm")

        db.collection("Emergency Contacts")
            .document("$finalMunicipalityForm")
            .collection("Firefighters")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error retrieving firefighters: ${error.message}", error)
                    return@addSnapshotListener
                }

                val firefighters = mutableListOf<FirefighterAdmin>()
                snapshot?.documents?.forEach { document ->
                    val documentId = document.id
                    val contactNumber = document.getString("contacts") ?: ""
                    firefighters.add(FirefighterAdmin(documentId, contactNumber))

                    Log.d(TAG, "Retrieved firefighter: $documentId -> $contactNumber")
                }

            //    firefightersLiveData.setValue(firefighters)
                _firefighters.postValue(firefighters)
            }
    }

}



@SuppressLint("UnrememberedMutableState", "SuspiciousIndentation",
    "StateFlowValueCalledInComposition"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AdminFirefightersScreen(navController: NavController?, selectedMunicipality: String?) {
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

    //  var firefighters = finalMunicipalityForm?.let { getEmergencyPhoneNumber(it) }


    val viewModel = EmergencyViewModel()
    if (finalMunicipalityForm != null) {
        viewModel.getEmergencyPhoneNumber(finalMunicipalityForm)
    }

    // Call getDeviceLocation within a composable function
    LaunchedEffect(true) {
        locationHelper.getDeviceLocation { location ->
            userLocation = location
            Log.d(
                "Location_ShareFirePage",
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

    val newFirefighterDocument = hashMapOf("contacts" to mutableNewContactNumber.value)


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
                if (finalMunicipalityForm != null) {
                    FirefightersHeaderComponent(navController, finalMunicipalityForm)
                }
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
                        Text("Map Placeholder", textAlign = TextAlign.Center)
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
                    Text(
                        text = "Firefighters",
                        color = Color(0xFFFFFFFF),
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                        // Adjust the horizontal padding as needed
                    )
                }


                Spacer(modifier = Modifier.height(8.dp))

                val firefightersList by viewModel.firefighters.observeAsState(initial = emptyList())

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


                            if (firefightersList.isEmpty()) {
                                // Show placeholder text or view indicating no data
                                Log.d("ViewModel", "AdminFirePage: Empty")
                                // Show loading indicator
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }

                            } else {
                                for (firefighter in firefightersList) {
                                    val documentId = firefighter.documentId
                                    var contactNumber = firefighter.contactNumber
                                    var isEditing by remember { mutableStateOf(false) }
                                    Spacer(modifier = Modifier.height(5.dp))

                                    Column {

                                        Text(
                                            text = firefighter.documentId,
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


                                            val editedContactNumber =
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
                                                contentAlignment = Alignment.Center
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

                                                    textFieldIdValue.value = firefighter.documentId
                                                    textFieldContactValue.value =
                                                        firefighter.contactNumber

                                                    AlertDialog(
                                                        onDismissRequest = {
                                                            showDialog.value = false
                                                        },
                                                        title = { Text("Update Document Data") },

                                                        text = {
                                                            // The form inside the AlertDialog
                                                            Column {
                                                                OutlinedTextField(
                                                                    value = textFieldIdValue.value,
                                                                    onValueChange = {
                                                                        textFieldIdValue.value = it
                                                                    },
                                                                    label = { Text("Changing  Document ID will result in deletion to re-update Document ID ") }

                                                                )

                                                                OutlinedTextField(
                                                                    value = textFieldContactValue.value,
                                                                    onValueChange = { newValue ->
                                                                        // Limit the length to 11 digits
                                                                        if (newValue.length <= 11) {
                                                                            textFieldContactValue.value =
                                                                                newValue
                                                                        }

                                                                        // Validate the input
                                                                        if (newValue.isNotEmpty() && newValue.all { char -> char.isDigit() }) {
                                                                            if (newValue.length == 11) {
                                                                                // Valid contact number
                                                                            } else {
                                                                                // Show error message
                                                                                Log.e(
                                                                                    "ContactNumber",
                                                                                    "Invalid contact number. Please enter a 11-digit number."
                                                                                )
                                                                            }
                                                                        } else {
                                                                            // Show error message
                                                                            Log.e(
                                                                                "ContactNumber",
                                                                                "Please enter only digits."
                                                                            )
                                                                        }


                                                                    },

                                                                    keyboardOptions = KeyboardOptions.Default.copy(
                                                                        keyboardType = KeyboardType.Number
                                                                    ),
                                                                    label = { Text("Input Contact Number") }

                                                                )

                                                                // Add more form elements here if needed
                                                            }
                                                        },
                                                        confirmButton = {
                                                            Button(
                                                                onClick = {
                                                                    // Handle the form submission
                                                                    if (firefighter.documentId != textFieldIdValue.value || firefighter.contactNumber != textFieldContactValue.value) {
                                                                        val db = Firebase.firestore

                                                                        // Check if document ID is untainted
                                                                        val docRef =
                                                                            db.collection("Emergency Contacts")
                                                                                .document("$finalMunicipalityForm")
                                                                                .collection("Firefighters")
                                                                                .document(
                                                                                    firefighter.documentId
                                                                                )
                                                                        docRef.get()
                                                                            .addOnSuccessListener { snapshot ->
                                                                                if (snapshot.exists()) {
                                                                                    // Document exists and is not tainted
                                                                                    if (firefighter.documentId != textFieldIdValue.value) {

                                                                                        // Document ID changed, delete and re-insert with updated data
                                                                                        db
                                                                                            .collection(
                                                                                                "Emergency Contacts"
                                                                                            )
                                                                                            .document(
                                                                                                "$finalMunicipalityForm"
                                                                                            )
                                                                                            .collection(
                                                                                                "Firefighters"
                                                                                            )
                                                                                            .document(
                                                                                                firefighter.documentId
                                                                                            )
                                                                                            .delete()
                                                                                            .addOnSuccessListener {

                                                                                                Log.d(
                                                                                                    "DeleteDocument",
                                                                                                    "Document deleted successfully"
                                                                                                )
                                                                                                // Insert new data with updated document ID and contact number
                                                                                                var newDocumentId =
                                                                                                    textFieldIdValue.value
                                                                                                var contactNumber =
                                                                                                    textFieldContactValue.value

                                                                                                if (contactNumber.isEmpty()) {
                                                                                                    // Use the existing contact number
                                                                                                    contactNumber =
                                                                                                        firefighter.contactNumber
                                                                                                    //   newDocumentId = firefighter.documentId
                                                                                                }

                                                                                                db.collection(
                                                                                                    "Emergency Contacts"
                                                                                                )
                                                                                                    .document(
                                                                                                        "$finalMunicipalityForm"
                                                                                                    )
                                                                                                    .collection(
                                                                                                        "Firefighters"
                                                                                                    )
                                                                                                    .document(
                                                                                                        newDocumentId
                                                                                                    )
                                                                                                    .set(
                                                                                                        hashMapOf(
                                                                                                            "contacts" to contactNumber,
                                                                                                        )
                                                                                                    )
                                                                                                    .addOnSuccessListener {
                                                                                                        Log.d(
                                                                                                            "InsertDocument",
                                                                                                            "New document inserted successfully"
                                                                                                        )
                                                                                                        textFieldIdValue.value =
                                                                                                            ""
                                                                                                        textFieldContactValue.value =
                                                                                                            ""
                                                                                                        showDialog.value =
                                                                                                            false
                                                                                                        Toast.makeText(
                                                                                                            context,
                                                                                                            "Document updated successfully",
                                                                                                            Toast.LENGTH_SHORT
                                                                                                        )
                                                                                                            .show()

                                                                                                    }
                                                                                                    .addOnFailureListener { e ->
                                                                                                        Log.e(
                                                                                                            "InsertDocument",
                                                                                                            "Error inserting document",
                                                                                                            e
                                                                                                        )
                                                                                                    }
                                                                                            }
                                                                                            .addOnFailureListener { e ->
                                                                                                Log.e(
                                                                                                    "DeleteDocument",
                                                                                                    "Error deleting document",
                                                                                                    e
                                                                                                )
                                                                                            }
                                                                                    } else {


                                                                                        // Only contact number changed, update existing document
                                                                                        db.collection(
                                                                                            "Emergency Contacts"
                                                                                        )
                                                                                            .document(
                                                                                                "$finalMunicipalityForm"
                                                                                            )
                                                                                            .collection(
                                                                                                "Firefighters"
                                                                                            )
                                                                                            .document(
                                                                                                firefighter.documentId
                                                                                            )
                                                                                            .update(
                                                                                                "contacts",
                                                                                                textFieldContactValue.value
                                                                                            )
                                                                                            .addOnSuccessListener {
                                                                                                Log.d(
                                                                                                    "UpdateContact",
                                                                                                    "Contact number updated successfully"
                                                                                                )
                                                                                                showDialog.value =
                                                                                                    false
                                                                                                textFieldIdValue.value =
                                                                                                    ""
                                                                                                textFieldContactValue.value =
                                                                                                    ""
                                                                                                Toast.makeText(
                                                                                                    context,
                                                                                                    "Contact number updated successfully",
                                                                                                    Toast.LENGTH_SHORT
                                                                                                )
                                                                                                    .show()
                                                                                            }
                                                                                            .addOnFailureListener { e ->
                                                                                                Log.e(
                                                                                                    "UpdateContact",
                                                                                                    "Error updating contact number",
                                                                                                    e
                                                                                                )
                                                                                            }
                                                                                    }
                                                                                } else {
                                                                                    // Document does not exist or is tainted
                                                                                    // Show error message
                                                                                    Toast.makeText(
                                                                                        context,
                                                                                        "Document not found or corrupted. Please try again.",
                                                                                        Toast.LENGTH_SHORT
                                                                                    ).show()
                                                                                }
                                                                            }
                                                                            .addOnFailureListener { e ->
                                                                                Log.e(
                                                                                    "GetDocument",
                                                                                    "Error getting document",
                                                                                    e
                                                                                )
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
                                                        text = firefighter.contactNumber,
                                                        color = Color.Black,
                                                        style = LocalTextStyle.current
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
                                                        // Delete the contact number from Firestore
                                                        if (finalMunicipalityForm != null) {
                                                            val db = Firebase.firestore

                                                            db
                                                                .collection("Emergency Contacts")
                                                                .document("$finalMunicipalityForm")
                                                                .collection("Firefighters")
                                                                .document(firefighter.documentId)
                                                                .delete()
                                                                .addOnSuccessListener {
                                                                    Log.d(
                                                                        "DeleteNumber",
                                                                        "Number deleted successfully"
                                                                    )
                                                                    isEditing = false
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Number Deleted Successfully",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                                .addOnFailureListener { e ->
                                                                    Log.e(
                                                                        "DeleteNumber",
                                                                        "Error deleting number",
                                                                        e
                                                                    )
                                                                }
                                                        } else {
                                                            //         context.showToast("Error")
                                                        }
                                                    }
                                            )

                                            Spacer(modifier = Modifier.width(2.dp))

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
                                                                phoneNumber = contactNumber
                                                            )
                                                        } ?: run {
                                                            Log.w(
                                                                "Clickable",
                                                                "User location is null"
                                                            )
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