package io.agora.agorauikit_android.AgoraRtmController

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

public interface RtmTokenCallback {
    fun onSuccess(token: String)
    fun onError(error: RtmTokenError)
}

/**
 * Error types to expect from fetchToken on failing ot retrieve valid token.
 */
enum class RtmTokenError {
    NO_DATA, INVALID_DATA, INVALID_URL, UNKNOWN
}

@ExperimentalUnsignedTypes
fun AgoraRtmController.Companion.fetchToken(urlBase: String, rtmId: String, completion: RtmTokenCallback) {
    val log: Logger = Logger.getLogger("AgoraVideoUIKit")
    val client = OkHttpClient()
    val url = "$urlBase/rtm/$rtmId"
    val request: okhttp3.Request = Request.Builder()
        .url(url)
        .method("GET", null)
        .build()

    try {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                log.log(Level.WARNING, "Unexpected code ${e.localizedMessage}")
                completion.onError(RtmTokenError.INVALID_DATA)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    val jObject = JSONObject(it)
                    val token = jObject.getString("rtmToken")
                    if (!token.isEmpty()) {
                        completion.onSuccess(token)
                        return
                    }
                }
                completion.onError(RtmTokenError.NO_DATA)
            }
        })
    } catch (e: IOException) {
        log.log(Level.WARNING, e.localizedMessage)
        completion.onError(RtmTokenError.INVALID_URL)
    } catch (e: JSONException) {
        log.log(Level.WARNING, e.localizedMessage)
        completion.onError(RtmTokenError.NO_DATA)
    } catch (e: Exception) {
        log.log(Level.WARNING, e.message)
        completion.onError(RtmTokenError.UNKNOWN)
    }
}
