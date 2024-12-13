package com.example.gravitofp

import android.content.Context
import android.content.SharedPreferences
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.UUID

class GravitoFPApiClient(private val context: Context, parentDomain: String, origin: String) {
    private val client = OkHttpClient()
    private var apiEndpoint: String = "https://gto.${parentDomain}/api/v3/firstparty"
    private var origin: String = "https://${origin}"

    @Throws(IOException::class)
    fun get(url: String, headers: Map<String, String> = emptyMap()): String? {
        val requestBuilder = Request.Builder().url(url)
        for (header in headers) {
            requestBuilder.addHeader(header.key, header.value)
        }
        val request = requestBuilder.build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            println(response.body)
            return response.body?.string() ?: throw IOException("Response body is null")
        }
    }

    @Throws(IOException::class)
    fun post(url: String, json: String, headers: Map<String, String> = emptyMap()): String? {
        val body = json.toRequestBody(JSON)
        val requestBuilder = Request.Builder().url(url)
        for (header in headers) {
            if (header.value != "null") {
                requestBuilder.addHeader(header.key, header.value)
            }
        }
        val request = requestBuilder.post(body).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body?.string()
        }
    }

    private fun generateGUID(): String {
        return UUID.randomUUID().toString()
    }

    // exposed functions
    @Throws(Exception::class)
    fun getConsentData(): String {
        val matchOnId = getMatchOnId()
        val json = """
        {
            "e": {
                "matchOnId": "${matchOnId}"
            },
            "k": [],
            "c": []
        }
    """.trimIndent()
        val localId = retrieveData(context, "gravitoFPID")

        val response = this.post(
            this.apiEndpoint,
            json,
            headers = mapOf("Origin" to this.origin, "gm-id" to localId.toString())
        )

        val jsonObject = JSONObject(response)
        val id = jsonObject.get("i")
        storeData(context, "gravitoFPID", id.toString())
        val eventsData= JSONObject(jsonObject.get("e").toString())

        return eventsData.get("consentData").toString()
    }

    @Throws(Exception::class)
    fun updateConsentData(consentData: String): String {
        val matchOnId = getMatchOnId()
        val json = """
        {
            "e": {
                "matchOnId": "${matchOnId}",
                "consentData": "${consentData}"
            },
            "k": [],
            "c": []
        }
    """.trimIndent()
        val localId = retrieveData(context, "gravitoFPID")

        val response = this.post(
            this.apiEndpoint,
            json,
            headers = mapOf("Origin" to this.origin, "gm-id" to localId.toString())
        )

        val jsonObject = JSONObject(response)
        val id = jsonObject.get("i")
        storeData(context, "gravitoFPID", id.toString())


        val eventsData= JSONObject(jsonObject.get("e").toString())

        return eventsData.get("consentData").toString()
    }

    private fun getMatchOnId(): String {
        val id = retrieveData(context, "gravitoMatchOnId")
        if (id != null) {
            return id
        }
        val newID = generateGUID() + "-hmd"
        storeData(context, "gravitoMatchOnId", newID)
        return newID
    }

    private fun storeData(context: Context, key: String, value: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    private fun retrieveData(context: Context, key: String): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

    companion object {
        private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    }
}