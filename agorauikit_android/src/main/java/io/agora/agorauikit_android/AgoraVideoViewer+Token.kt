package io.agora.agorauikit_android

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

public interface TokenCallback {
    fun onSuccess(token: String)
    fun onError(error: TokenError)
}

/**
 * Error types to expect from fetchToken on failing ot retrieve valid token.
 */
enum class TokenError {
    NODATA, INVALIDDATA, INVALIDURL
}

/**
 * Requests the token from our backend token service
 * @param urlBase: base URL specifying where the token server is located
 * @param channelName: Name of the channel we're requesting for
 * @param userId: User ID of the user trying to join (0 for any user)
 * @param callback: Callback method for returning either the string token or error
 */
@ExperimentalUnsignedTypes
fun AgoraVideoViewer.Companion.fetchToken(urlBase: String, channelName: String, userId: Int, completion: TokenCallback) {
    val log: Logger = Logger.getLogger("AgoraVideoUIKit")
    val client = OkHttpClient()
    val url = "$urlBase/rtc/$channelName/publisher/uid/$userId/"
    val request: okhttp3.Request = Request.Builder()
        .url(url)
        .method("GET", null)
        .build()
    try {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                log.log(Level.WARNING, "Unexpected code ${e.localizedMessage}")
                completion.onError(TokenError.INVALIDDATA)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    val jObject = JSONObject(it)
                    val token = jObject.getString("rtcToken")
                    if (token.isNotEmpty()) {
                        completion.onSuccess(token)
                        return
                    }
                }
                completion.onError(TokenError.NODATA)
            }
        }
        )
    } catch (e: IOException) {
        log.log(Level.WARNING, e.localizedMessage)
        completion.onError(TokenError.INVALIDURL)
    } catch (e: JSONException) {
        log.log(Level.WARNING, e.localizedMessage)
        completion.onError(TokenError.INVALIDDATA)
    }
}

/**
 * Renews the token before the default expiry time or the specified time
 */
@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.fetchRenewToken() {
    (this.agoraSettings.tokenURL)?.let { tokenURL ->
        this.connectionData.channel?.let { channelName ->
            val callback: TokenCallback = object : TokenCallback {
                override fun onSuccess(token: String) {
                    this@fetchRenewToken.agkit.renewToken(token)
                }

                override fun onError(error: TokenError) {
                    Logger.getLogger("AgoraVideoUIKit", error.name)
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
