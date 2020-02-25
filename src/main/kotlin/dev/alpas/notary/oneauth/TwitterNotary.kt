package dev.alpas.notary.oneauth

import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.model.OAuthRequest
import dev.alpas.Environment
import dev.alpas.JsonSerializer
import dev.alpas.http.HttpCall

open class TwitterNotary(
    call: HttpCall,
    override val apiKey: String,
    override val apiSecret: String,
    override val callback: String? = null,
    scope: String? = null
) : OneAuthNotary(call, scope) {

    protected open val userDetailsUrl = "https://api.twitter.com/1.1/account/verify_credentials.json"

    override fun user(): TwitterUser {
        return TwitterUser(fetchUserDetails())
    }

    constructor(call: HttpCall, env: Environment, scope: String? = null) : this(
        call,
        env("TWITTER_API_KEY")!!,
        env("TWITTER_API_SECRET")!!,
        env("TWITTER_API_CALLBACK"),
        scope
    )

    override fun apiService(): TwitterApi = TwitterApi.instance()

    protected open fun fetchUserDetails(): Map<String, Any?> {
        val token = accessToken()
        val details = makeRequest(userDetailsUrl, token)
        return JsonSerializer.deserialize<MutableMap<String, Any?>>(details).also {
            it["access_token"] = token.token
            it["access_token_secret"] = token.tokenSecret
        }
    }

    override fun visit(request: OAuthRequest) {
        request.addQuerystringParameter("include_entities", "false")
        request.addQuerystringParameter("skip_status", "true")
        request.addQuerystringParameter("include_email", "true")
    }
}
