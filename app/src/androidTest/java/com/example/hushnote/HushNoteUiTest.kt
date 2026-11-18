package com.example.hushnote

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HushNoteUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAuthScreenDisplaysSuccessfully() {
        // Verify HushNote branding and sign in elements are displayed on launch
        composeTestRule.onNodeWithText("HushNote").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email address").assertIsDisplayed()
    }
}
