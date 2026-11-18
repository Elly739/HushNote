package com.example.hushnote

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesDashboardScreen(
    userEmail: String,
    dbHelper: DatabaseHelper,
    onOpenNoteEditor: (Long?) -> Unit
) {
    val context = LocalContext.current
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var dailyQuote by remember { mutableStateOf(NetworkUtils.Quote("Loading inspiration...", "HushNote")) }

    // Load notes and quote
    LaunchedEffect(userEmail) {
        withContext(Dispatchers.IO) {
            notes = dbHelper.getAllNotes(userEmail)
            dailyQuote = NetworkUtils.fetchDailyQuote()
        }
    }

    val filteredNotes = notes.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.body.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HushNote · ${notes.size} notes saved") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenNoteEditor(null) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Daily Inspiration Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DAILY INSPIRATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${dailyQuote.content}\"",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "— ${dailyQuote.author}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search notes…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredNotes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "No notes found", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Tap + to create your first secure note.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenNoteEditor(note.id) },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = note.title.ifEmpty { "Untitled Note" }, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = note.body.take(60) + if (note.body.length > 60) "..." else "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = note.updatedAt, style = MaterialTheme.typography.labelSmall)
                                }

                                Row {
                                    // Implicit Intent Share
                                    IconButton(onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_SUBJECT, note.title)
                                            putExtra(Intent.EXTRA_TEXT, "${note.title}\n\n${note.body}")
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "Share Secure Note")
                                        context.startActivity(shareIntent)
                                    }) {
                                        Icon(Icons.Default.Share, contentDescription = "Share Note")
                                    }

                                    IconButton(onClick = {
                                        dbHelper.deleteNote(note.id)
                                        notes = dbHelper.getAllNotes(userEmail)
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Note", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
