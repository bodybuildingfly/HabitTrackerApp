package com.example.habittrackerapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {

    val auth = FirebaseAuth.getInstance()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Implement the login screen UI
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.35f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Login Screen",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.65f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current

            // Text field for username input
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(.8f),
                value = username,
                onValueChange = { username = it },
                label = { Text("User name") }
            )
            // Text field for password input
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(.8f),
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button to trigger login
                Button(
                    onClick = {
                        // Format username to sample format
                        val email = "$username@example.com"

                        // Login with Firebase Auth
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Login successful, navigate to home screen
                                    onLoginSuccess()

                                } else {
                                    // Login failed, display error message
                                    val errorMessage = task.exception?.message ?: "Login failed"
                                    Toast.makeText(
                                        context,
                                        errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                ) {
                    Text("Login")
                }
                // Button to sign up
                Button(
                    onClick = {
                        // Format username to sample format
                        val email = "$username@example.com"

                        // Sign up with Firebase Auth
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Sign up successful, navigate to home
                                    Toast.makeText(
                                        context,
                                        "Sign up successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Navigate to home screen


                                    // Handle sign up success
                                } else {
                                    // Sign up failed, display error message
                                    val errorMessage =
                                        task.exception?.message ?: "Sign up failed"
                                    Toast.makeText(
                                        context,
                                        errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                ) {
                    Text("Sign up")
                }
            }
        }
    }
}