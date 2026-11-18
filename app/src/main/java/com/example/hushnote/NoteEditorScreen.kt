package com.example.hushnote

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    userEmail: String,
    noteId: Long?,
    dbHelper: DatabaseHelper,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    LaunchedEffect(noteId) {
        if (noteId != null && noteId != -1L) {
            val notes = dbHelper.getAllNotes(userEmail)
            val note = notes.find { it.id == noteId }
            if (note != null) {
                title = note.title
                body = SecurityUtils.decryptNote(note.body, userEmail)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null || noteId == -1L) "New Secure Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                        val encryptedBody = SecurityUtils.encryptNote(body, userEmail)
                        if (noteId == null || noteId == -1L) {
                            dbHelper.addNote(userEmail, title, encryptedBody, currentDate)
                        } else {
                            dbHelper.updateNote(noteId, title, encryptedBody, currentDate)
                        }
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save Note")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Note Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text("Write your secure note here…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}
