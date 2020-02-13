package dev.alpas.notary.oneauth

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.builder.api.DefaultApi10a
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService
import dev.alpas.http.HttpCall
import dev.alpas.notary.Notary
import dev.alpas.orAbort

internal const val REQUEST_TOKEN_SESSION_NAME = "notary.requestToken"

/**
 * An abstract Notary class that should be extended by API services that support the OAuth1 protocol such as Twitter.
 */
abstract class OneAuthNotary(protected val call: HttpCall) : Notary {
    protected lateinit var service: OAuth10aService
        private set

    /**
     * The actual API service to be used to notarize the user.
     *
     * @return An API service provider - one of
     */
    protected abstract fun apiService(): DefaultApi10a

    override fun build(builder: ServiceBuilder) {
        visit(builder)
        service = builder.build(apiService())
    }

    override fun redirect() {
        val url = service.use { authService ->
            val requestToken = authService.requestToken.also { saveRequestTokenInSession(it) }
            authService.getAuthorizationUrl(requestToken)
        }
        call.redirect().toExternal(url)
    }

    /**
     * Save the given request token object in the session.
     *
     * @param requestToken A request token object to be saved.
     */
    protected open fun saveRequestTokenInSession(requestToken: OAuth1RequestToken) {
        call.session.put(REQUEST_TOKEN_SESSION_NAME, requestToken)
    }

    protected open fun fetchRequestTokenFromSession(): OAuth1RequestToken {
        return call.session.pull<OAuth1RequestToken>(
            REQUEST_TOKEN_SESSION_NAME
        ).orAbort()
    }

    /**
     * Return an access token by looking for an oauth_verifier parameter
     * in the call and getting the request token from the the session.
     *
     * @return The access token.
     */
    protected open fun accessToken(): OAuth1AccessToken {
        val verifier = call.stringParam("oauth_verifier").orAbort()
        return service.getAccessToken(fetchRequestTokenFromSession(), verifier)
    }

    /**
     * Make a request with the API service at the given url endpoint.
     *
     * @param url The endpoint.
     * @param token The access token. If empty will be pulled from the current session.
     * @param verb The HTTP method to be used for making the request.
     */
    protected open fun makeRequest(url: String, token: OAuth1AccessToken? = null, verb: Verb = Verb.GET): String {
        val request = OAuthRequest(verb, url).also {
            visit(it)
            service.signRequest(token ?: accessToken(), it)
        }

        return service.use { it.execute(request).body }
    }

    /**
     * Visit the given builder before it gets build.
     * @param builder An instance of ServiceBuilder to add extra information, if any.
     */
    protected open fun visit(builder: ServiceBuilder) {}

    /**
     * Visit the given request before it gets sent over to the API provider.
     * @param request An instance of OAuthRequest to add extra parameters, if any.
     */
    protected open fun visit(request: OAuthRequest) {}
}
