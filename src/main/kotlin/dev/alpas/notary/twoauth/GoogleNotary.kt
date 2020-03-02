package dev.alpas.notary.twoauth

import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.model.OAuthRequest
import dev.alpas.Environment
import dev.alpas.JsonSerializer
import dev.alpas.http.HttpCall

open class GoogleNotary(
    call: HttpCall,
    override val apiKey: String,
    override val apiSecret: String,
    override val callback: String? = null,
    scope: String? = null,
    additionalParams: Map<String, String>? = null
) : TwoAuthNotary(call, scope, additionalParams) {

    protected open val userDetailsUrl = "https://www.googleapis.com/oauth2/v3/userinfo"

    override fun user(): GoogleUser {
        return GoogleUser(fetchUserDetails())
    }

    constructor(call: HttpCall, env: Environment, scope: String? = null, additionalParams: Map<String, String>? = null) : this(
        call,
        env("GOOGLE_API_KEY")!!,
        env("GOOGLE_API_SECRET")!!,
        env("GOOGLE_API_CALLBACK"),
        scope,
        additionalParams
    )

    override fun apiService(): GoogleApi20 = GoogleApi20.instance()

    protected open fun fetchUserDetails(): Map<String, Any?> {
        val token = accessToken()
        val details = makeRequest(userDetailsUrl, token)
        return JsonSerializer.deserialize<MutableMap<String, Any?>>(details).also {
            it["access_token"] = token.accessToken
            it["refresh_token"] = token.refreshToken
            it["token_expires_in"] = token.expiresIn
            it["token_scope"] = token.scope
        }
    }
}
