package com.example.emcontacts.interfaces

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.emcontacts.Screen
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(drawerState: DrawerState, navController: NavController?) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFFFD317)
        ),
        title = {
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isOpen) {
                        drawerState.close()
                    } else {
                        drawerState.open()
                    }
                }
            }) {
                Icon(
                    contentDescription = "Menu",
                    imageVector = Icons.Filled.Menu,
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFF000000)
                )
            }
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    color = Color(0xFF000000),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.padding(end = 4.dp),
                    overflow = TextOverflow.Ellipsis,
                    text = "Common User"
                )
                IconButton(onClick = {
                    navController?.navigate(Screen.AdminLogin.route)
                }) {
                    Icon(
                        contentDescription = "Account Circle",
                        imageVector = Icons.Filled.AccountCircle,
                        modifier = Modifier.size(38.dp),
                        tint = Color(0xFF000000)
                    )
                }
            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ContactsHeader() {
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
    )
}