package com.example.hushnote

import org.junit.Assert.*
import org.junit.Test

class AuthValidationTest {

    @Test
    fun testEmailValidation() {
        assertTrue(SecurityUtils.isValidEmail("student@hushnote.com"))
        assertTrue(SecurityUtils.isValidEmail("test.user+tag@domain.co.uk"))
        assertFalse(SecurityUtils.isValidEmail("invalid-email"))
        assertFalse(SecurityUtils.isValidEmail(""))
        assertFalse(SecurityUtils.isValidEmail(null))
    }

    @Test
    fun testIsNotEmpty() {
        assertTrue(SecurityUtils.isNotEmpty("SecureNote"))
        assertFalse(SecurityUtils.isNotEmpty(""))
        assertFalse(SecurityUtils.isNotEmpty(null))
        assertFalse(SecurityUtils.isNotEmpty("   "))
    }

    @Test
    fun testPasswordHashingAndVerification() {
        val password = "StrongPassword123!"
        val (salt, hash) = SecurityUtils.hashPassword(password)

        assertNotNull(salt)
        assertNotNull(hash)
        assertTrue(SecurityUtils.verifyPassword(password, salt, hash))
        assertFalse(SecurityUtils.verifyPassword("WrongPassword", salt, hash))
    }

    @Test
    fun testAesEncryptionAndDecryption() {
        val secretKey = "user@hushnote.com"
        val originalText = "Top Secret Research Notes"

        val encrypted = SecurityUtils.encryptNote(originalText, secretKey)
        assertNotEquals(originalText, encrypted)

        val decrypted = SecurityUtils.decryptNote(encrypted, secretKey)
        assertEquals(originalText, decrypted)
    }
}
