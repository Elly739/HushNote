package com.example.hushnote

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun AuthScreen(
    dbHelper: DatabaseHelper,
    onLoginSuccess: (String) -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Custom Drawing View Integration (LockShieldCustomView)
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { context -> LockShieldCustomView(context) },
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "HushNote",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Your thoughts. Under lock.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email address") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "Hide" else "Show")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { -20 }),
                exit = fadeOut()
            ) {
                if (errorMessage != null) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            AnimatedVisibility(
                visible = successMessage != null,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { -20 }),
                exit = fadeOut()
            ) {
                if (successMessage != null) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = successMessage!!, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    errorMessage = null
                    successMessage = null

                    if (!SecurityUtils.isNotEmpty(email) || !SecurityUtils.isNotEmpty(password)) {
                        errorMessage = "Email and password cannot be empty."
                        return@Button
                    }
                    if (!SecurityUtils.isValidEmail(email)) {
                        errorMessage = "Please enter a valid email address."
                        return@Button
                    }

                    if (isSignUp) {
                        val (salt, hash) = SecurityUtils.hashPassword(password)
                        val success = dbHelper.registerUser(email, salt, hash)
                        if (success) {
                            successMessage = "Account created successfully! Please sign in."
                            isSignUp = false
                        } else {
                            errorMessage = "Registration failed. Email may already exist."
                        }
                    } else {
                        val credentials = dbHelper.getUserCredentials(email)
                        if (credentials != null) {
                            val (salt, expectedHash) = credentials
                            if (SecurityUtils.verifyPassword(password, salt, expectedHash)) {
                                onLoginSuccess(email)
                            } else {
                                errorMessage = "Invalid password."
                            }
                        } else {
                            errorMessage = "User not found. Please sign up first."
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(if (isSignUp) "Create Account" else "Sign In", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                isSignUp = !isSignUp
                errorMessage = null
                successMessage = null
            }) {
                Text(if (isSignUp) "Already have an account? Sign in" else "Don't have an account? Sign up")
            }
        }
    }
}
