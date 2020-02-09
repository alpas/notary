package dev.alpas.notary

import com.github.scribejava.core.builder.ServiceBuilder

/**
 * An interface to be implemented by OAuth1 or OAuth2 services.
 */
interface Notary {
    /**
     * The API Key to be used for the actual OAuth service.
     */
    val apiKey: String

    /**
     * The API Secret to be used for the actual OAuth service.
     */
    val apiSecret: String

    /**
     * The callback URL to be included in the actual OAuth service request.
     */
    val callback: String?
        get() = null

    /**
     * Build the given ServiceBuilder with the actual API service.
     */
    fun build(builder: ServiceBuilder)

    /**
     * Redirect the current call for notarization.
     */
    fun redirect()

    /**
     * The notarized user.
     *
     * @return The notarized User.
     */
    fun user(): User
}
