package dev.alpas.notary.twoauth

import com.github.scribejava.apis.GitHubApi
import com.github.scribejava.core.model.OAuthRequest
import dev.alpas.Environment
import dev.alpas.JsonSerializer
import dev.alpas.http.HttpCall

open class GitHubNotary(
    call: HttpCall,
    override val apiKey: String,
    override val apiSecret: String,
    override val callback: String? = null
) : TwoAuthNotary(call) {

    protected open val userDetailsUrl = "https://api.github.com/user"

    override fun user(): GitHubUser {
        return GitHubUser(fetchUserDetails())
    }

    constructor(call: HttpCall, env: Environment) : this(
        call,
        env("GITHUB_API_KEY")!!,
        env("GITHUB_API_SECRET")!!,
        env("GITHUB_API_CALLBACK")
    )

    override fun apiService(): GitHubApi = GitHubApi.instance()

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

    override fun visit(request: OAuthRequest) {
        request.addQuerystringParameter("scope", "user")
    }
}
