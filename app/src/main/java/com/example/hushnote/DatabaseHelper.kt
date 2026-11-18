package com.example.hushnote

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Note(
    val id: Long,
    val userEmail: String,
    val title: String,
    val body: String,
    val updatedAt: String
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "hushnote.db"
        private const val DATABASE_VERSION = 1

        // Users Table
        const val TABLE_USERS = "users"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_SALT = "salt"
        const val COLUMN_PASSWORD_HASH = "password_hash"

        // Notes Table
        const val TABLE_NOTES = "notes"
        const val COLUMN_NOTE_ID = "id"
        const val COLUMN_NOTE_USER_EMAIL = "user_email"
        const val COLUMN_NOTE_TITLE = "title"
        const val COLUMN_NOTE_BODY = "body"
        const val COLUMN_NOTE_UPDATED_AT = "updated_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_EMAIL + " TEXT PRIMARY KEY,"
                + COLUMN_SALT + " TEXT NOT NULL,"
                + COLUMN_PASSWORD_HASH + " TEXT NOT NULL)")

        val createNotesTable = ("CREATE TABLE " + TABLE_NOTES + "("
                + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTE_USER_EMAIL + " TEXT NOT NULL,"
                + COLUMN_NOTE_TITLE + " TEXT,"
                + COLUMN_NOTE_BODY + " TEXT,"
                + COLUMN_NOTE_UPDATED_AT + " TEXT)")

        db.execSQL(createUsersTable)
        db.execSQL(createNotesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    // User Authentication CRUD with Parameterized Queries
    fun registerUser(email: String, salt: String, passwordHash: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_SALT, salt)
            put(COLUMN_PASSWORD_HASH, passwordHash)
        }
        val result = db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        return result != -1L
    }

    fun getUserCredentials(email: String): Pair<String, String>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_SALT, $COLUMN_PASSWORD_HASH FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?",
            arrayOf(email)
        )
        var credentials: Pair<String, String>? = null
        if (cursor.moveToFirst()) {
            val salt = cursor.getString(0)
            val hash = cursor.getString(1)
            credentials = Pair(salt, hash)
        }
        cursor.close()
        return credentials
    }

    fun updateUserPassword(email: String, salt: String, passwordHash: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SALT, salt)
            put(COLUMN_PASSWORD_HASH, passwordHash)
        }
        return db.update(TABLE_USERS, values, "$COLUMN_EMAIL = ?", arrayOf(email))
    }

    // Notes CRUD Operations with Parameterized Queries
    fun addNote(userEmail: String, title: String, body: String, updatedAt: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_USER_EMAIL, userEmail)
            put(COLUMN_NOTE_TITLE, title)
            put(COLUMN_NOTE_BODY, body)
            put(COLUMN_NOTE_UPDATED_AT, updatedAt)
        }
        return db.insert(TABLE_NOTES, null, values)
    }

    fun updateNote(noteId: Long, title: String, body: String, updatedAt: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_TITLE, title)
            put(COLUMN_NOTE_BODY, body)
            put(COLUMN_NOTE_UPDATED_AT, updatedAt)
        }
        return db.update(TABLE_NOTES, values, "$COLUMN_NOTE_ID = ?", arrayOf(noteId.toString()))
    }

    fun deleteNote(noteId: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NOTES, "$COLUMN_NOTE_ID = ?", arrayOf(noteId.toString()))
    }

    fun getAllNotes(userEmail: String): List<Note> {
        val notes = mutableListOf<Note>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_NOTE_ID, $COLUMN_NOTE_USER_EMAIL, $COLUMN_NOTE_TITLE, $COLUMN_NOTE_BODY, $COLUMN_NOTE_UPDATED_AT FROM $TABLE_NOTES WHERE $COLUMN_NOTE_USER_EMAIL = ? ORDER BY $COLUMN_NOTE_ID DESC",
            arrayOf(userEmail)
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(0)
                val email = cursor.getString(1)
                val title = cursor.getString(2)
                val body = cursor.getString(3)
                val updatedAt = cursor.getString(4)
                notes.add(Note(id, email, title, body, updatedAt))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return notes
    }
}
