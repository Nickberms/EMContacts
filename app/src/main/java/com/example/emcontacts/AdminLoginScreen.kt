package com.example.emcontacts

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminLoginScreen(navController: NavController?) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    var isEmailEmpty by remember { mutableStateOf(false) }
    var isPasswordEmpty by remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF171A1F))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.65f)
                .background(Color(0xFF1E2128))
                .size(2.dp)
                .border(1.dp, Color(0xFFFFD317))
                .align(Alignment.Center)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .offset(y = (115).dp),
            ) {
                Text(
                    text = "ADMIN LOGIN",
                    color = Color(0xFFFFD317),
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(145.dp))
                InputTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isEmailEmpty = it.isEmpty()
                    },
                    label = "Email",
                    placeholder = "Enter your email",
                    keyboardType = KeyboardType.Text,
                    isError = isEmailEmpty
                )
                Spacer(modifier = Modifier.height(10.dp))
                InputTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        isPasswordEmpty = it.isEmpty()
                    },
                    label = "Password",
                    placeholder = "Enter your password",
                    keyboardType = KeyboardType.Password,
                    isError = isPasswordEmpty,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        isEmailEmpty = email.isEmpty()
                        isPasswordEmpty = password.isEmpty()
                        if (!isEmailEmpty && !isPasswordEmpty) {
                            performLogin(email, password) { isSuccess, errorMessage ->
                                if (isSuccess) {
                                    navController?.navigate(Screen.AdminHomeScreen.route)
                                } else {
                                    loginError = errorMessage
                                    isDialogVisible = true
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFCFD2DA),
                    ),
                ) {
                    Text(text = "Verify", color = Color(0xFF000000))
                }
                if (loginError.isNotEmpty() && isDialogVisible) {
                    AlertDialog(
                        onDismissRequest = { isDialogVisible = false },
                        title = { Text(text = "Invalid Credentials") },
                        text = { Text(text = "Invalid email or password.") },
                        confirmButton = {
                            Button(onClick = { isDialogVisible = false }) {
                                Text("OK")
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Not an admin? Go back now",
                    color = Color(0xFFFFD317),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable { navController?.navigate(Screen.Home.route) }
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(25.dp))
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_person),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.Center)
                .offset(y = (-215).dp)
                .background(Color(0xFFFFD317), shape = CircleShape)
        )
    }
}

@Composable
fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val containerColor = Color(0xFF323743)
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(text = label, color = Color(0xFFFFFFFF)) },
        placeholder = { Text(text = placeholder, color = Color(0xFFFFFFFF)) },
        isError = isError,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFFFFFFFF),
            unfocusedTextColor = Color(0xFFFFFFFF),
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            cursorColor = Color(0xFFFFFFFF),
            focusedBorderColor = Color(0xFFFFD317),
            unfocusedBorderColor = Color(0xFFFFD317),
        ),
        visualTransformation = visualTransformation
    )
    if (isError) {
        Text(
            text = "",
            color = Color.Red,
            fontSize = 0.1.sp
        )
    }
}

fun performLogin(username: String, password: String, onResult: (Boolean, String) -> Unit) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, "")
            } else {
                val message = task.exception?.message ?: "Invalid username or password"
                onResult(false, message)
            }
        }
}