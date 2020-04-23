package dev.alpas.notary.twoauth

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import dev.alpas.exceptions.NotFoundHttpException
import dev.alpas.http.HttpCall
import dev.alpas.notary.Notary
import dev.alpas.orAbort
import dev.alpas.secureRandomString

internal const val NOTARY_STATE_SESSION_NAME = "notary.state"

abstract class TwoAuthNotary(
    protected val call: HttpCall,
    private val scope: String? = null,
    private val additionalParams: Map<String, String>? = null
) : Notary {
    private lateinit var service: OAuth20Service
    protected abstract fun apiService(): DefaultApi20

    override fun build(builder: ServiceBuilder) {
        visit(builder)
        if (!scope.isNullOrBlank()) {
            builder.withScope(scope)
        }
        service = builder.build(apiService())
    }

    override fun redirect(params: Map<String, String>, headers: Map<String, String>) {
        val url = service.use { authService ->
            val state = secureRandomString(25)
            saveStateInSession(state)
            authService.createAuthorizationUrlBuilder().also {
                it.additionalParams((additionalParams ?: emptyMap()) + params)
                it.state(state)
            }.build()
        }
        call.redirect().toExternal(url, headers = headers)
    }

    /**
     * Make a request with the API service at the given url endpoint.
     *
     * @param url The endpoint.
     * @param token The access token. If empty will be pulled from the current session.
     * @param verb The HTTP method to be used for making the request.
     */
    protected open fun makeRequest(url: String, token: OAuth2AccessToken? = null, verb: Verb = Verb.GET): String {
        val request = OAuthRequest(verb, url).also {
            visit(it)
            service.signRequest(token ?: accessToken(), it)
        }

        return service.use { it.execute(request).body }
    }

    /**
     * Return an access token by looking for a code parameter in the call and matching the secret.
     *
     * @return The access token.
     */
    protected open fun accessToken(): OAuth2AccessToken {
        val receivedSecret = call.string("state")
        val sentSecret = fetchStateFromSession().orAbort()
        if (sentSecret != receivedSecret) {
            throw NotFoundHttpException()
        }
        val code = call.string("code")
        return service.getAccessToken(code)
    }

    /**
     * Save the given state in the session.
     *
     * @param state A secret stated to be saved.
     */
    protected open fun saveStateInSession(state: String) {
        call.session.put(NOTARY_STATE_SESSION_NAME, state)
    }

    /**
     * Fetch the state from the session.
     */
    protected open fun fetchStateFromSession(): String? {
        return call.session.pull(NOTARY_STATE_SESSION_NAME)
    }

    /**
     * Visit the given builder before it gets build.
     * @param builder An instance of [ServiceBuilder] to add extra information, if any.
     */
    protected open fun visit(builder: ServiceBuilder) {}

    /**
     * Visit the given request before it gets sent over to the API provider.
     * @param request An instance of [OAuthRequest] to add extra parameters, if any.
     */
    protected open fun visit(request: OAuthRequest) {}
}
