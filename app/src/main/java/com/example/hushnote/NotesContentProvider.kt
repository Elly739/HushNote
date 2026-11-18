package com.example.hushnote

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class NotesContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.hushnote.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/notes")

        private const val NOTES = 1
        private const val NOTE_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "notes", NOTES)
            addURI(AUTHORITY, "notes/#", NOTE_ID)
        }
    }

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(): Boolean {
        context?.let {
            dbHelper = DatabaseHelper(it)
            return true
        }
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        val cursor = when (uriMatcher.match(uri)) {
            NOTES -> db.query(DatabaseHelper.TABLE_NOTES, projection, selection, selectionArgs, null, null, sortOrder)
            NOTE_ID -> {
                val id = uri.lastPathSegment
                db.query(DatabaseHelper.TABLE_NOTES, projection, "${DatabaseHelper.COLUMN_NOTE_ID} = ?", arrayOf(id), null, null, sortOrder)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            NOTES -> "vnd.android.cursor.dir/vnd.$AUTHORITY.notes"
            NOTE_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.notes"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        val id = when (uriMatcher.match(uri)) {
            NOTES -> db.insert(DatabaseHelper.TABLE_NOTES, null, values)
            else -> throw IllegalArgumentException("Unsupported URI for insertion: $uri")
        }
        if (id > 0) {
            context?.contentResolver?.notifyChange(uri, null)
            return Uri.withAppendedPath(CONTENT_URI, id.toString())
        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        val count = when (uriMatcher.match(uri)) {
            NOTES -> db.delete(DatabaseHelper.TABLE_NOTES, selection, selectionArgs)
            NOTE_ID -> {
                val id = uri.lastPathSegment
                db.delete(DatabaseHelper.TABLE_NOTES, "${DatabaseHelper.COLUMN_NOTE_ID} = ?", arrayOf(id))
            }
            else -> throw IllegalArgumentException("Unsupported URI for deletion: $uri")
        }
        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        val count = when (uriMatcher.match(uri)) {
            NOTES -> db.update(DatabaseHelper.TABLE_NOTES, values, selection, selectionArgs)
            NOTE_ID -> {
                val id = uri.lastPathSegment
                db.update(DatabaseHelper.TABLE_NOTES, values, "${DatabaseHelper.COLUMN_NOTE_ID} = ?", arrayOf(id))
            }
            else -> throw IllegalArgumentException("Unsupported URI for update: $uri")
        }
        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }
}
