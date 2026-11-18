# HushNote Oral Presentation & Assessment Study Guide

This guide is designed for your group members to prepare for individual oral assessment questions during the presentation next week. Every topic is mapped directly to its exact file location and implementation logic in HushNote.

---

## 1. SQLite Database Structure & CRUD Operations
- **Where to find it**: [DatabaseHelper.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/java/com/example/hushnote/DatabaseHelper.kt)
- **Key Questions & Answers**:
  - **Q: How is SQLite set up?**
    *A*: By subclassing `SQLiteOpenHelper`, defining table schemas (`users` and `notes`) in `onCreate()`, and managing database versions in `onUpgrade()`.
  - **Q: How do you prevent SQL Injection?**
    *A*: By using parameterized queries with `?` placeholders (e.g., `db.rawQuery("SELECT ... WHERE email = ?", arrayOf(email))`) instead of string concatenation.
  - **Q: What CRUD methods are implemented?**
    *A*: `registerUser()`, `getUserCredentials()`, `updateUserPassword()`, `addNote()`, `updateNote()`, `deleteNote()`, and `getAllNotes()`.

---

## 2. Input Validation & Password Hashing / Salting
- **Where to find it**: [SecurityUtils.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/java/com/example/hushnote/SecurityUtils.kt), [AuthScreen.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/java/com/example/hushnote/AuthScreen.kt)
- **Key Questions & Answers**:
  - **Q: Where is input validation performed?**
    *A*: In `SecurityUtils.isValidEmail()` and `SecurityUtils.isNotEmpty()`, tested in `AuthValidationTest.kt`.
  - **Q: How are passwords hashed securely?**
    *A*: We generate a 16-byte random salt using `SecureRandom`, combine it with the password, and hash it using `MessageDigest.getInstance("SHA-256")`. Both salt and hash are stored in Base64 format in SQLite.

---

## 3. AES Encryption at Rest for Notes
- **Where to find it**: [SecurityUtils.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/java/com/example/hushnote/SecurityUtils.kt)
- **Key Questions & Answers**:
  - **Q: How are notes encrypted?**
    *A*: Using AES encryption (`Cipher.getInstance("AES/ECB/PKCS5Padding")`) with a key derived from the user's email/secret. `encryptNote()` encrypts plaintext before saving to SQLite, and `decryptNote()` decrypts it when loaded in `NotesDashboardScreen.kt` and `NoteEditorScreen.kt`.

---

## 4. Activities, Navigation & Services
- **Where to find it**: [MainActivity.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/java/com/example/hushnote/MainActivity.kt), [NoteBackupService.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/java/com/example/hushnote/NoteBackupService.kt)
- **Key Questions & Answers**:
  - **Q: How is bottom navigation implemented?**
    *A*: Using Jetpack Compose `NavigationBar` and `NavigationBarItem` in `MainActivity.kt` switching between Notes, Account, and Settings.
  - **Q: What is the Started Service used for?**
    *A*: `NoteBackupService` runs in the background (`Dispatchers.IO`) to serialize notes into an encrypted JSON backup file in internal storage (`filesDir`) when triggered from Settings.

---

## 5. Broadcast Receivers & Intent Filters
- **Where to find it**: [ConnectivityReceiver.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/java/com/example/hushnote/ConnectivityReceiver.kt), [AndroidManifest.xml](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/AndroidManifest.xml)
- **Key Questions & Answers**:
  - **Q: How is the BroadcastReceiver registered?**
    *A*: Declared in `AndroidManifest.xml` inside `<receiver>` with an `<intent-filter>` for `ACTION_BOOT_COMPLETED`.

---

## 6. Content Provider & Data Sharing
- **Where to find it**: [NotesContentProvider.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/java/com/example/hushnote/NotesContentProvider.kt)
- **Key Questions & Answers**:
  - **Q: How does the Content Provider work?**
    *A*: It exposes notes via Content URI (`content://com.example.hushnote.provider/notes`) and implements `query()`, `insert()`, `update()`, `delete()`, and `getType()`.
  - **Q: How is data access controlled?**
    *A*: `android:exported="false"` in `AndroidManifest.xml` ensures only the app itself can access the provider.

---

## 7. Networking, JSON & XML Parsing
- **Where to find it**: [NetworkUtils.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/java/com/example/hushnote/NetworkUtils.kt)
- **Key Questions & Answers**:
  - **Q: How do you fetch data from a web API?**
    *A*: Using `HttpURLConnection` on a background coroutine thread (`Dispatchers.IO`), parsing the response using `JSONObject`, and falling back gracefully to a cached quote if offline.

---

## 8. Telephony API
- **Where to find it**: [AccountScreen.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/java/com/example/hushnote/AccountScreen.kt)
- **Key Questions & Answers**:
  - **Q: How is telephony used?**
    *A*: `TelephonyManager` is used to retrieve the network operator name and display connectivity status on the Account Details screen.

---

## 9. Custom Drawing (`onDraw()`) & Animations
- **Where to find it**: [LockShieldCustomView.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/java/com/example/hushnote/LockShieldCustomView.kt), [NotesDashboardScreen.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/main/java/com/example/hushnote/NotesDashboardScreen.kt)
- **Key Questions & Answers**:
  - **Q: Where is custom drawing implemented?**
    *A*: In `LockShieldCustomView`, overriding `onDraw()` with `Canvas` and `Paint` to draw the security shield and lock emblem.
  - **Q: What animations are included?**
    *A*: Compose `AnimatedVisibility` and slide/fade transitions for note cards and error messages.

---

## 10. Testing & Quality Assurance
- **Where to find it**: [AuthValidationTest.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/test/java/com/example/hushnote/AuthValidationTest.kt), [HushNoteUiTest.kt](file:///C:/Users/Okatho/AndroidStudioProjects/HushNote/app/src/androidTest/java/com/example/hushnote/HushNoteUiTest.kt)
- **Key Questions & Answers**:
  - **Q: What tests were written?**
    *A*: JUnit unit tests for input validation, password hashing, and AES encryption, plus Compose Espresso UI tests for authentication.
