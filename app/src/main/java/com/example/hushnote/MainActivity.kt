package com.example.hushnote

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hushnote.ui.theme.HushNoteTheme

class MainActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPrefs = getSharedPreferences("hushnote_prefs", Context.MODE_PRIVATE)
        val savedTheme = sharedPrefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        dbHelper = DatabaseHelper(this)

        setContent {
            var themeMode by remember { mutableStateOf(savedTheme) }
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                AppCompatDelegate.MODE_NIGHT_YES -> true
                AppCompatDelegate.MODE_NIGHT_NO -> false
                else -> systemDark
            }

            HushNoteTheme(darkTheme = darkTheme) {
                var currentUserEmail by remember { mutableStateOf<String?>(null) }
                var currentTab by remember { mutableStateOf(0) } // 0: Notes, 1: Account, 2: Settings
                var editingNoteId by remember { mutableStateOf<Long?>(null) }
                var isEditing by remember { mutableStateOf(false) }

                if (currentUserEmail == null) {
                    AuthScreen(
                        dbHelper = dbHelper,
                        onLoginSuccess = { email ->
                            currentUserEmail = email
                        }
                    )
                } else {
                    if (isEditing) {
                        NoteEditorScreen(
                            userEmail = currentUserEmail!!,
                            noteId = editingNoteId,
                            dbHelper = dbHelper,
                            onNavigateBack = {
                                isEditing = false
                                editingNoteId = null
                            }
                        )
                    } else {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 8.dp
                                ) {
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.Note, contentDescription = "Notes") },
                                        label = { Text("Notes") },
                                        selected = currentTab == 0,
                                        onClick = { currentTab = 0 }
                                    )
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
                                        label = { Text("Account") },
                                        selected = currentTab == 1,
                                        onClick = { currentTab = 1 }
                                    )
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                        label = { Text("Settings") },
                                        selected = currentTab == 2,
                                        onClick = { currentTab = 2 }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding)) {
                                when (currentTab) {
                                    0 -> NotesDashboardScreen(
                                        userEmail = currentUserEmail!!,
                                        dbHelper = dbHelper,
                                        onOpenNoteEditor = { noteId ->
                                            editingNoteId = noteId
                                            isEditing = true
                                        }
                                    )
                                    1 -> AccountScreen(
                                        userEmail = currentUserEmail!!,
                                        dbHelper = dbHelper,
                                        onLogout = {
                                            currentUserEmail = null
                                            currentTab = 0
                                        }
                                    )
                                    2 -> SettingsScreen(
                                        userEmail = currentUserEmail!!,
                                        onThemeChanged = { newTheme ->
                                            themeMode = newTheme
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
