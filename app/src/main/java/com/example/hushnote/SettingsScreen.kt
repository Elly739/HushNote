package com.example.hushnote

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userEmail: String
) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("hushnote_prefs", Context.MODE_PRIVATE)
    var selectedTheme by remember {
        mutableStateOf(sharedPrefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM))
    }
    var backupStatus by remember { mutableStateOf("Ready to backup") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Application Settings") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Section
            Text(text = "Theme Mode", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = selectedTheme == AppCompatDelegate.MODE_NIGHT_NO,
                    onClick = {
                        selectedTheme = AppCompatDelegate.MODE_NIGHT_NO
                        sharedPrefs.edit().putInt("theme_mode", selectedTheme).apply()
                        AppCompatDelegate.setDefaultNightMode(selectedTheme)
                    },
                    label = { Text("Light") }
                )
                FilterChip(
                    selected = selectedTheme == AppCompatDelegate.MODE_NIGHT_YES,
                    onClick = {
                        selectedTheme = AppCompatDelegate.MODE_NIGHT_YES
                        sharedPrefs.edit().putInt("theme_mode", selectedTheme).apply()
                        AppCompatDelegate.setDefaultNightMode(selectedTheme)
                    },
                    label = { Text("Dark") }
                )
                FilterChip(
                    selected = selectedTheme == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                    onClick = {
                        selectedTheme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        sharedPrefs.edit().putInt("theme_mode", selectedTheme).apply()
                        AppCompatDelegate.setDefaultNightMode(selectedTheme)
                    },
                    label = { Text("System") }
                )
            }

            HorizontalDivider()

            // Secure Backup Section
            Text(text = "Secure Backup", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text(text = "Trigger a background service to write encrypted notes to internal storage.", style = MaterialTheme.typography.bodyMedium)
            Button(
                onClick = {
                    val intent = Intent(context, NoteBackupService::class.java).apply {
                        putExtra("user_email", userEmail)
                    }
                    context.startService(intent)
                    backupStatus = "Backup service triggered successfully."
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Backup Notes Now")
            }
            Text(text = backupStatus, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)

            HorizontalDivider()

            // Privacy Statement Section
            Text(text = "Privacy Statement", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text(
                text = "HushNote stores your personal notes securely with AES encryption at rest and SHA-256 salted password hashing. Your private thoughts remain strictly confidential and under your control.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}
