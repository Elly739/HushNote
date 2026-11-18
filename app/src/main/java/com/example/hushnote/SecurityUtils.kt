package com.example.hushnote

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object SecurityUtils {

    // Input Validation Functions (Extracted for reusability and testing)
    fun isNotEmpty(value: String?): Boolean {
        return !value.isNullOrBlank()
    }

    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return email.matches(emailRegex)
    }

    // Password Hashing with Random Salt (SHA-256 + Salt)
    fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    fun hashPassword(password: String, salt: ByteArray = generateSalt()): Pair<String, String> {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))

        val encoder = Base64.getEncoder()
        val saltBase64 = encoder.encodeToString(salt)
        val hashBase64 = encoder.encodeToString(hashBytes)

        return Pair(saltBase64, hashBase64)
    }

    fun verifyPassword(password: String, saltBase64: String, expectedHashBase64: String): Boolean {
        try {
            val decoder = Base64.getDecoder()
            val salt = decoder.decode(saltBase64)
            val digest = MessageDigest.getInstance("SHA-256")
            digest.update(salt)
            val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            val computedHashBase64 = Base64.getEncoder().encodeToString(hashBytes)
            return computedHashBase64 == expectedHashBase64
        } catch (e: Exception) {
            return false
        }
    }

    // AES Encryption for Note Content at Rest
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"

    private fun getSecretKey(keyString: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(keyString.toByteArray(Charsets.UTF_8))
        val aesKeyBytes = keyBytes.copyOf(16)
        return SecretKeySpec(aesKeyBytes, ALGORITHM)
    }

    fun encryptNote(content: String, userKey: String): String {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(userKey))
            val encryptedBytes = cipher.doFinal(content.toByteArray(Charsets.UTF_8))
            Base64.getEncoder().encodeToString(encryptedBytes)
        } catch (e: Exception) {
            content
        }
    }

    fun decryptNote(encryptedContent: String, userKey: String): String {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(userKey))
            val decodedBytes = Base64.getDecoder().decode(encryptedContent)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            encryptedContent
        }
    }
}
