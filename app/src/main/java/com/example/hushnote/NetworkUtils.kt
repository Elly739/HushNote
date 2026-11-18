package com.example.hushnote

import android.util.Log
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {

    private const val TAG = "NetworkUtils"
    private const val QUOTE_API_URL = "https://api.quotable.io/random"

    data class Quote(val content: String, val author: String)

    fun fetchDailyQuote(): Quote {
        try {
            val url = URL(QUOTE_API_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = reader.readLine() } != null) {
                    response.append(line)
                }
                // Also handle single read loop if BufferedReader needs standard iteration
                val fullResponse = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }

                val json = JSONObject(fullResponse.ifEmpty { response.toString() })
                val content = json.optString("content", "Great things are done by a series of small things brought together.")
                val author = json.optString("author", "Vincent Van Gogh")
                return Quote(content, author)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Network fetch failed, using offline cached quote: ${e.message}")
        }

        // Offline Fallback
        return Quote(
            "Privacy is not an option, and it shouldn't be the price we accept for getting our information.",
            "Gary Kovacs"
        )
    }

    // Demonstrate XML Parsing requirement
    fun parseXmlFeed(xmlString: String): List<String> {
        val items = mutableListOf<String>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(xmlString.reader())

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "title") {
                    items.add(parser.nextText())
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e(TAG, "XML Parsing error", e)
        }
        return items
    }
}
