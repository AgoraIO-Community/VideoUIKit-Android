package io.agora.agorauikit_android

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

public interface TokenCallback {
    fun onSuccess(token: String)
    fun onError(error: TokenError)
}

enum class TokenError {
    NODATA, INVALIDDATA, INVALIDURL
}

@ExperimentalUnsignedTypes
fun AgoraVideoViewer.Companion.fetchToken(urlBase: String, channelName: String, userId: Int, completion: TokenCallback) {
    val log: Logger = Logger.getLogger("AgoraUIKit")
    val client = OkHttpClient()
    val url = "$urlBase/rtc/$channelName/publisher/uid/$userId/"
    val request: okhttp3.Request = Request.Builder()
            .url(url)
            .method("GET", null)
            .build()
    try {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful()) {
                log.log(Level.WARNING, "Unexpected code $response")
                completion.onError(TokenError.INVALIDDATA)
            } else {
                response.body()?.string()?.let {
                    val jObject = JSONObject(it)
                    val token = jObject.getString("rtcToken")
                    if (!token.isEmpty()) {
                        completion.onSuccess(token)
                        return
                    }
                }
                completion.onError(TokenError.NODATA)
            }
            return
        }
    } catch (e: IOException) {
        log.log(Level.WARNING, e.localizedMessage)
        completion.onError(TokenError.INVALIDURL)
    } catch (e: JSONException) {
        log.log(Level.WARNING, e.localizedMessage)
        completion.onError(TokenError.INVALIDDATA)
    }
}

@ExperimentalUnsignedTypes
fun AgoraVideoViewer.fetchRenewToken() {
    (this.agoraSettings.tokenURL)?.let { tokenURL ->
        this.connectionData.channel?.let { channelName ->
            val callback: TokenCallback = object : TokenCallback {
                override fun onSuccess(token: String) {
                    this@fetchRenewToken.agkit.renewToken(token)
                }

                override fun onError(error: TokenError) {
                    Logger.getLogger("AgoraUIKit", error.name)
                }
            }

            AgoraVideoViewer.fetchToken(
                tokenURL,
                channelName,
                this.userID,
                callback
            )
        }
    }
}
