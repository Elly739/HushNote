package com.example.hushnote

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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

    LaunchedEffect(userEmail) {
        withContext(Dispatchers.IO) {
            notes = dbHelper.getAllNotes(userEmail)
            dailyQuote = NetworkUtils.fetchDailyQuote()
        }
    }

    val filteredNotes = notes.filter {
        val decryptedBody = SecurityUtils.decryptNote(it.body, userEmail)
        it.title.contains(searchQuery, ignoreCase = true) || decryptedBody.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("HushNote · ${notes.size} secured", style = MaterialTheme.typography.titleMedium)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenNoteEditor(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            // Daily Inspiration Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "DAILY INSPIRATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${dailyQuote.content}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "— ${dailyQuote.author}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search encrypted notes…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            AndroidView(
                                factory = { context -> LockShieldCustomView(context) },
                                modifier = Modifier.size(52.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "No secure notes found", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap + to create your first encrypted note.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        val decryptedBody = SecurityUtils.decryptNote(note.body, userEmail)
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(400)) + slideInVertically(animationSpec = tween(400), initialOffsetY = { 40 })
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenNoteEditor(note.id) },
                                shape = RoundedCornerShape(18.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = note.title.ifEmpty { "Untitled Note" },
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Row {
                                            IconButton(
                                                onClick = {
                                                    val sendIntent = Intent().apply {
                                                        action = Intent.ACTION_SEND
                                                        putExtra(Intent.EXTRA_SUBJECT, note.title)
                                                        putExtra(Intent.EXTRA_TEXT, "${note.title}\n\n$decryptedBody")
                                                        type = "text/plain"
                                                    }
                                                    val shareIntent = Intent.createChooser(sendIntent, "Share Secure Note")
                                                    context.startActivity(shareIntent)
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = "Share Note", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                            }

                                            Spacer(modifier = Modifier.width(4.dp))

                                            IconButton(
                                                onClick = {
                                                    dbHelper.deleteNote(note.id)
                                                    notes = dbHelper.getAllNotes(userEmail)
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete Note", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = decryptedBody.take(90) + if (decryptedBody.length > 90) "..." else "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Encrypted at rest",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                            )
                                        }

                                        Text(
                                            text = note.updatedAt,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
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
}
