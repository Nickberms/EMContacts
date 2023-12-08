package com.example.emcontacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.emcontacts.AdminRetrievalScreen.AdminFirePage
import com.example.emcontacts.AdminRetrievalScreen.AdminMedicPage
import com.example.emcontacts.AdminRetrievalScreen.AdminRescuePage
import com.example.emcontacts.admin.AdminHomeScreen
import com.example.emcontacts.admin.AdminPolicePage

import com.example.emcontacts.screens.AdminLoginScreen
import com.example.emcontacts.screens.HomeScreen
import com.example.emcontacts.ui.theme.EMContactsTheme

import com.google.firebase.FirebaseApp

sealed class Screen(val route: String) {

    object AdminLogin : Screen("admin_login")
    object AdminHomeScreen : Screen("adminHome")


    object Home : Screen("home")
    object MedicsScreen : Screen("MedicsScreen")
    object FirefightersScreen : Screen("FirefightersScreen")
    object PoliceScreen : Screen("PoliceScreen")
    object RescuersScreen : Screen("RescuersScreen")



    object AdminMedicPage : Screen("AdminMedicPage")
    object AdminFirePage : Screen("AdminFirePage")
    object AdminPolicePage : Screen("AdminPolicePage")
    object AdminRescuePage : Screen("AdminRescuePage")





}



class MainActivity : ComponentActivity() { 
    private val REQUEST_LOCATION_PERMISSION = 1
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            startApp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startApp()
                } else {
                    Toast.makeText(this, "Location permission not granted", Toast.LENGTH_LONG)
                        .show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(
                            this,
                            "You can grant the app location permission in Settings",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }, 4000)
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    private fun startApp() {
        FirebaseApp.initializeApp(this)
        Log.d("Firebase", "Firebase initialized successfully")

        setContent {
            val navController = rememberNavController()
            EMContactsTheme {

                NavHost(navController = navController, startDestination = Screen.Home.route) {

                    composable(Screen.Home.route) {
                        HomeScreen(navController)
                    }

                    composable(Screen.AdminHomeScreen.route) {
                        AdminHomeScreen(navController)
                    }

                    composable(Screen.AdminLogin.route) {
                        AdminLoginScreen(navController)
                    }



                    composable(
                        route = "${Screen.MedicsScreen.route}/{municipalityName}",
                        arguments = listOf(navArgument("municipalityName") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val municipalityName = backStackEntry.arguments?.getString("municipalityName")
                        MedicsScreen(navController = navController, selectedMunicipality = municipalityName)
                    }

                    composable(
                        route = "${Screen.FirefightersScreen.route}/{municipalityName}",
                        arguments = listOf(navArgument("municipalityName") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val municipalityName = backStackEntry.arguments?.getString("municipalityName")
                        FirefightersScreen()
                    }

                    composable(
                        route = "${Screen.PoliceScreen.route}/{municipalityName}",
                        arguments = listOf(navArgument("municipalityName") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val municipalityName = backStackEntry.arguments?.getString("municipalityName")
                        PoliceScreen(navController = navController, selectedMunicipality = municipalityName)
                    }
                    composable(
                        route = "${Screen.RescuersScreen.route}/{municipalityName}",
                        arguments = listOf(navArgument("municipalityName") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val municipalityName = backStackEntry.arguments?.getString("municipalityName")
                        RescuersScreen(navController = navController, selectedMunicipality = municipalityName)
                    }




                    composable(
                        route = "${Screen.AdminMedicPage.route}/{municipalityName}",
                        arguments = listOf(navArgument("municipalityName") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val municipalityName = backStackEntry.arguments?.getString("municipalityName")
                        AdminMedicPage(navController = navController, selectedMunicipality = municipalityName)
                    }

                    composable(
                        route = "${Screen.AdminFirePage.route}/{municipalityName}",
                        arguments = listOf(navArgument("municipalityName") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val municipalityName = backStackEntry.arguments?.getString("municipalityName")
                        AdminFirePage(navController = navController, selectedMunicipality = municipalityName)
                    }

                    composable(
                        route = "${Screen.AdminPolicePage.route}/{municipalityName}",
                        arguments = listOf(navArgument("municipalityName") { type = NavType.StringType })
                    ) { backStackEntry ->
                        backStackEntry.arguments?.getString("municipalityName")
                        AdminPolicePage()
                    }

                    composable(
                        route = "${Screen.AdminRescuePage.route}/{municipalityName}",
                        arguments = listOf(navArgument("municipalityName") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val municipalityName = backStackEntry.arguments?.getString("municipalityName")
                        AdminRescuePage(navController = navController, selectedMunicipality = municipalityName)
                    }




                }

                }

            }
        }
    }
