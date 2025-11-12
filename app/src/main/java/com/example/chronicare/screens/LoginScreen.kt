package com.example.chronicare.screens

import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chronicare.R


@Composable
fun LoginScreen(navController: NavController, viewModel: SharedData) {
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validateInputs(): Boolean {
        var isValid = true

        if (!Patterns.EMAIL_ADDRESS.matcher(viewModel.email).matches()) {
            emailError = "Please enter a valid email address"
            isValid = false
        } else {
            emailError = null
        }

        if (viewModel.password.isBlank()) {
            passwordError = "Password cannot be empty"
            isValid = false
        } else {
            passwordError = null
        }

        return isValid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF007F7A))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(200.dp)
                .padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Title
        Text(
            text = "Login",
            fontSize = 28.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "Welcome to Chronic Care Wellness Track",
            color = Color.White,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email Field
        OutlinedTextField(
            value = viewModel.email,
            onValueChange = {
                viewModel.email = it
                if (emailError != null) validateInputs()
            },
            label = {
                Text(
                    text = "Email",
                    color = if (emailError != null) MaterialTheme.colorScheme.error else Color.Gray
                )
            },
            isError = emailError != null,
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
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp, bottom = 8.dp)
            )
        } ?: Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = {
                viewModel.password = it
                if (passwordError != null) validateInputs()
            },
            label = {
                Text(
                    text = "Password",
                    color = if (passwordError != null) MaterialTheme.colorScheme.error else Color.Gray
                )
            },
            isError = passwordError != null,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedBorderColor = if (passwordError != null) Color.Red else Color(0xFF007F7A),
                unfocusedBorderColor = if (passwordError != null) Color.Red else Color.Gray,
                errorBorderColor = Color.Red,
                cursorColor = Color(0xFF007F7A),
                errorLabelColor = Color.Red
            )
        )

        passwordError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp, bottom = 8.dp)
            )
        } ?: Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        Button(
            onClick = {
                if (validateInputs()) {
                    navController.navigate("home")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004C45))
        ) {
            Text("LOGIN", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Up Prompt
        Text(
            text = "Don't have an account? Sign Up",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable { navController.navigate("signup") }
                .padding(8.dp)
        )
    }
}


