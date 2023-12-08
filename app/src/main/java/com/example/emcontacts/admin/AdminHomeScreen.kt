package com.example.emcontacts.admin

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.emcontacts.Screen
import com.example.emcontacts.interfaces.DrawerComponent
import com.example.emcontacts.interfaces.FirefightersButton
import com.example.emcontacts.interfaces.MedicsButton
import com.example.emcontacts.interfaces.Municipality
import com.example.emcontacts.interfaces.MunicipalityMap
import com.example.emcontacts.interfaces.PoliceButton
import com.example.emcontacts.interfaces.RescuersButton


@Composable
fun AdminHomeScreen(navController: NavController?) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    LocalInspectionMode.current
    var selectedMunicipality by remember { mutableStateOf<Municipality?>(null) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerComponent(drawerState) { municipality: Municipality ->
                selectedMunicipality = municipality
                Log.d("LOCATION_ADMIN", "HomeScreen: $municipality")
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
                AdminHomeHeader(drawerState, navController)
                Spacer(modifier = Modifier.height(16.dp))
                selectedMunicipality?.let { municipality ->
                    MunicipalityMap(
                        modifier = Modifier
                            .fillMaxHeight(0.3f), // 30% of the screen height
                        municipality = municipality
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
                        text = "EMERGENCY CONTACTS",
                        color = Color(0xFFFFFFFF),
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(9.dp))
                Box(
                    modifier = Modifier
                        .height(450.dp)
                        .padding(horizontal = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp)
                                    .background(Color(0xFFADD8E6))
                            ) {
                                MedicsButton {
                                    selectedMunicipality?.let { municipality ->
                                        navController?.navigate("${Screen.AdminMedicPage.route}/${municipality.name}")
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp)
                                    .background(Color.Gray)
                            ) {
                                PoliceButton {
                                    selectedMunicipality?.let { municipality ->
                                        navController?.navigate("${Screen.AdminPolicePage.route}/${municipality.name}")
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp)
                                    .background(Color.Green)
                            ) {
                                RescuersButton {
                                    selectedMunicipality?.let { municipality ->
                                        navController?.navigate("${Screen.AdminRescuePage.route}/${municipality.name}")
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp)
                                    .background(Color.Red)
                            ) {
                                FirefightersButton {
                                    selectedMunicipality?.let { municipality ->
                                        navController?.navigate("${Screen.AdminFirePage.route}/${municipality.name}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}