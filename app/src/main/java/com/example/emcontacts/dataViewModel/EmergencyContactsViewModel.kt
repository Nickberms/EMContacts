import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emcontacts.interfaces.Municipality
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class EmergencyContactsViewModel : ViewModel() {
    // Define MutableState variables for each contact number
    private val _contactNumber1 = mutableStateOf("")
    val contactNumber1: State<String> get() = _contactNumber1

    private val _contactNumber2 = mutableStateOf("")
    val contactNumber2: State<String> get() = _contactNumber2

    private val _contactNumber3 = mutableStateOf("")
    val contactNumber3: State<String> get() = _contactNumber3

    // Function to fetch emergency contact for a specific medic
    fun getEmergencyContact(medicId: String, municipality: Municipality?) {
        viewModelScope.launch {
            try {
                val snapshot = Firebase.firestore
                    .collection("Emergency Contacts")
                    .document(municipality.toString())
                    .collection("Medics")
                    .document(medicId)
                    .get()
                    .await()  // Use await to suspend until the data is fetched

                if (snapshot.exists()) {
                    when (medicId) {
                        "medicDC_1" -> _contactNumber1.value = snapshot.getString("contactnumMD1") ?: ""
                        "medicDC_2" -> _contactNumber2.value = snapshot.getString("contactnumMD2") ?: ""
                        "medicDC_3" -> _contactNumber3.value = snapshot.getString("contactnumMD3") ?: ""
                    }
                } else {
                    // Handle the case where the document doesn't exist
                    // You may want to provide default values or handle this differently
                }
            } catch (e: Exception) {
                // Handle exceptions, e.g., log or show an error message
                Log.e("EmergencyContactsViewModel", "Error getting emergency contact", e)
            }
        }
    }
}
