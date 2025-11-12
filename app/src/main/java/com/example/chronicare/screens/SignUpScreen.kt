package com.example.chronicare.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chronicare.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.navigation.NavController
import com.example.chronicare.homeScreens.DashboardScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, viewModel: SharedData) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

   // var name by remember { mutableStateOf("") }
 //   var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Validation error states
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var valid = true

        usernameError = if (viewModel.username.isBlank()) {
            valid = false
            "Name is required"
        } else null

        emailError = if (!Patterns.EMAIL_ADDRESS.matcher(viewModel.email).matches()) {
            valid = false
            "Enter a valid email"
        } else null

        passwordError = if (viewModel.password.length < 6) {
            valid = false
            "Password must be at least 6 characters"
        } else null

        confirmPasswordError = if (confirmPassword != viewModel.password) {
            valid = false
            "Passwords do not match"
        } else null

        return valid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App logo",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 8.dp)
        )

        // Title
        Text(
            text = "SIGN UP",
            color = Color(0xFF007F7A),
            fontSize = 26.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // -- Name Field --
        OutlinedTextField(
            value = viewModel.username,
            onValueChange = {
                viewModel.username = it
                if (usernameError != null) validate()
            },
            label = { Text("Name") },
            isError = usernameError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedBorderColor = if (emailError != null) Color.Red else Color(0xFF007F7A),
                unfocusedBorderColor = if (emailError != null) Color.Red else Color.Gray,
                errorBorderColor = Color.Red,
                cursorColor = Color(0xFF007F7A),
                errorLabelColor = Color.Red
            )
        )
        usernameError?.let {
            Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // -- Email Field --
        OutlinedTextField(
            value = viewModel.email,
            onValueChange = {
                viewModel.email = it
                if (emailError != null) validate()
            },
            label = { Text("Email") },
            isError = emailError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedBorderColor = if (emailError != null) Color.Red else Color(0xFF007F7A),
                unfocusedBorderColor = if (emailError != null) Color.Red else Color.Gray,
                errorBorderColor = Color.Red,
                cursorColor = Color(0xFF007F7A),
                errorLabelColor = Color.Red
            )
        )
        emailError?.let {
            Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // -- Password Field --
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = {
                viewModel.password = it
                if (passwordError != null) validate()
            },
            label = { Text("Password") },
            isError = passwordError != null,
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = if (passwordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedBorderColor = if (emailError != null) Color.Red else Color(0xFF007F7A),
                unfocusedBorderColor = if (emailError != null) Color.Red else Color.Gray,
                errorBorderColor = Color.Red,
                cursorColor = Color(0xFF007F7A),
                errorLabelColor = Color.Red
            )
        )
        passwordError?.let {
            Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // -- Confirm Password Field --
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                if (confirmPasswordError != null) validate()
            },
            label = { Text("Re-enter Password") },
            isError = confirmPasswordError != null,
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        painter = painterResource(id = if (confirmPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                        contentDescription = "Toggle confirm password visibility"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedBorderColor = if (emailError != null) Color.Red else Color(0xFF007F7A),
                unfocusedBorderColor = if (emailError != null) Color.Red else Color.Gray,
                errorBorderColor = Color.Red,
                cursorColor = Color(0xFF007F7A),
                errorLabelColor = Color.Red
            )
        )
        confirmPasswordError?.let {
            Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Up Button
        Button(
            onClick = {
                if (validate()) {

                    Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                    navController.navigate("home")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007F7A)),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text("SIGN UP", color = Color.White)
        }
    }
}

