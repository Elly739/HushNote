package com.example.hushnote

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

class NoteBackupService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val TAG = "NoteBackupService"
        const val ACTION_BACKUP_COMPLETED = "com.example.hushnote.BACKUP_COMPLETED"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Started service, not bound
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "NoteBackupService started. Performing secure backup...")

        val userEmail = intent?.getStringExtra("user_email") ?: "default_user@hushnote.com"

        serviceScope.launch {
            try {
                val dbHelper = DatabaseHelper(applicationContext)
                val notes = dbHelper.getAllNotes(userEmail)

                val backupFile = File(filesDir, "hushnote_backup_$userEmail.json")
                FileWriter(backupFile).use { writer ->
                    writer.write("[")
                    notes.forEachIndexed { index, note ->
                        val jsonNote = "{\"id\":${note.id}, \"title\":\"${note.title}\", \"updated_at\":\"${note.updatedAt}\"}"
                        writer.write(jsonNote)
                        if (index < notes.size - 1) writer.write(",")
                    }
                    writer.write("]")
                }
                Log.d(TAG, "Backup successfully written to ${backupFile.absolutePath}")

                // Send broadcast or notification about completion
                val broadcastIntent = Intent(ACTION_BACKUP_COMPLETED).apply {
                    putExtra("backup_path", backupFile.absolutePath)
                }
                sendBroadcast(broadcastIntent)

            } catch (e: Exception) {
                Log.e(TAG, "Backup failed", e)
            } finally {
                stopSelf(startId)
            }
        }

        return START_NOT_STICKY
    }
}
