package dev.alpas.notary

import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.model.OAuthRequest
import dev.alpas.Environment
import dev.alpas.JsonSerializer
import dev.alpas.http.HttpCall

open class TwitterNotary(
    call: HttpCall,
    override val apiKey: String,
    override val apiSecret: String,
    override val callback: String? = null
) : OneAuthNotary(call) {

    protected open val userDetailsUrl = "https://api.twitter.com/1.1/account/verify_credentials.json"

    override fun user(): TwitterUser {
        return TwitterUser(fetchUserDetails())
    }

    constructor(call: HttpCall, env: Environment) : this(
        call,
        env("TWITTER_API_KEY")!!,
        env("TWITTER_API_SECRET")!!,
        env("TWITTER_API_CALLBACK")
    )

    override fun apiService(): TwitterApi = TwitterApi.instance()

    protected open fun fetchUserDetails(): Map<String, Any?> {
        val details = makeRequest(userDetailsUrl)
        return JsonSerializer.deserialize(details)
    }

    override fun visit(request: OAuthRequest) {
        request.addQuerystringParameter("include_entities", "false")
        request.addQuerystringParameter("skip_status", "true")
        request.addQuerystringParameter("include_email", "true")
    }
}
