# HushNote · Secure Notes & Settings Application

> **Your thoughts. Under lock.**  
> *A commercially polished, privacy-focused personal notes application built for Android, integrating all core practical areas of modern Android development (CAT 3 Course Project).*

---

## 📱 Project Overview
**HushNote** is a secure, offline-capable personal notes application designed to protect user ideas and private information. Built around three pillars—**Privacy**, **Simplicity**, and **Control**—HushNote ensures zero plaintext storage of sensitive thoughts through AES encryption at rest and SHA-256 salted password authentication.

---

## 🏗️ Architecture & Component Mapping

| # | Project Feature / Course Topic | Corresponding File(s) | Core Implementation Details |
|---|---|---|---|
| 1 | **Environment Setup & Manifest** | `AndroidManifest.xml` | Declares Activity, Started Service, BroadcastReceiver, and ContentProvider with proper permissions. |
| 2 | **Secure User Authentication** | `DatabaseHelper.kt`, `SecurityUtils.kt`, `AuthScreen.kt` | SQLite database storing hashed passwords + random salt; reusable input validation (`isValidEmail`, `isNotEmpty`). |
| 3 | **Navigation, Activities & Services** | `MainActivity.kt`, `NoteBackupService.kt` | Bottom navigation bar (Notes, Account, Settings); started service for background encrypted note backups. |
| 4 | **Broadcast Receivers & Permissions** | `ConnectivityReceiver.kt` | BroadcastReceiver registered in Manifest with intent filter (`ACTION_BOOT_COMPLETED`). |
| 5 | **UI Design & Layouts** | `Theme.kt`, `Color.kt`, `res/values/` | Material 3 design with Midnight Navy (`#111D2C`) and Warm Amber (`#D9A85B`) brand palette. |
| 6 | **Drawing & Animation** | `LockShieldCustomView.kt` | Custom View using `onDraw()` to render the security lock and shield illustration. |
| 7 | **Notes Management (CRUD)** | `NotesDashboardScreen.kt`, `NoteEditorScreen.kt` | RecyclerView/LazyColumn equivalent listing notes, search bar, FAB, and share note via implicit intent (`ACTION_SEND`). |
| 8 | **Settings & Resources** | `SettingsScreen.kt` | Theme mode selector (Light, Dark, System) using `AppCompatDelegate` saved in `SharedPreferences`. |
| 9 | **Content Provider & Data Sharing** | `NotesContentProvider.kt` | Exposes notes via Content URI (`content://com.example.hushnote.provider/notes`) with restricted data access (`exported="false"`). |
| 10 | **Networking & API Integration** | `NetworkUtils.kt` | `HttpURLConnection` fetching daily inspirational quotes with JSON parsing (`JSONObject`) and XML parsing. |
| 11 | **Telephony & Deployment** | `AccountScreen.kt` | `TelephonyManager` integration displaying network operator status. |
| 12 | **Testing & Quality Assurance** | `AuthValidationTest.kt`, `HushNoteUiTest.kt` | JUnit unit tests for validation, password hashing, and AES encryption; Compose UI tests. |
| 13 | **Security Hardening** | `SecurityUtils.kt`, `DatabaseHelper.kt` | AES encryption at rest for notes; parameterized queries (`?`) preventing SQL injection. |
| 14 | **Project Integration** | `MainActivity.kt` | Fully integrated end-to-end application workflow. |

---

## 🔒 Security Architecture
- **Authentication**: Passwords are combined with a secure 16-byte random salt and hashed using **SHA-256**. Verification computes the hash using the stored salt.
- **Data at Rest**: Note contents are encrypted using **AES (Advanced Encryption Standard)** before being inserted into SQLite. When loaded, notes are decrypted on-the-fly.
- **SQL Injection Prevention**: All SQLite queries across `DatabaseHelper` and `NotesContentProvider` utilize parameterized SQL queries with `?` placeholders.

---

## 🧪 Testing & Verification
- Run unit tests:
  ```bash
  ./gradlew testDebugUnitTest
  ```
- Run build:
  ```bash
  ./gradlew assembleDebug
  ```

---

## 👥 Group & Presentation
Prepared for CAT 3 Group Presentation. Each group member can reference the code map above during oral assessment questions.
