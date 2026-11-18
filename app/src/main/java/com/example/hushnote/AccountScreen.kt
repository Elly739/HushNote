package com.example.hushnote

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    userEmail: String,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var operatorName by remember { mutableStateOf("Unknown Operator") }

    LaunchedEffect(Unit) {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            operatorName = telephonyManager?.networkOperatorName?.ifEmpty { "Wi-Fi / Cellular Connected" } ?: "Wi-Fi Connected"
        } catch (e: Exception) {
            operatorName = "Not Available"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Details") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Signed In User", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = userEmail, style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Telephony Network Operator", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = operatorName, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Sign Out")
            }
        }
    }
}
