package io.agora.agorauikit_android

/** Storing struct for holding data about the connection to Agora service
 * Create AgoraConnectionData object
 * @param appId: Agora App ID from https://agora.io
 * @param appToken: Token to be used to connect to a channel, can be nil.
 */
data class AgoraConnectionData @JvmOverloads constructor(
    var appId: String,
    var appToken: String? = null,
    var username: String? = null,
    var rtmToken: String? = null,
    var rtmId: String? = null,
    var rtmChannelName: String? = null
) {
    /**
     * Channel the object is connected to. This cannot be set with the initialiser
     */
    var channel: String? = null
}
