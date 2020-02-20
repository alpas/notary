package dev.alpas.notary

import com.github.scribejava.core.builder.ServiceBuilder
import dev.alpas.*
import dev.alpas.http.HttpCall

class NotaryServiceProvider : ServiceProvider {
    override fun register(app: Application) {
        // Register the default NotaryConfig if it is not already registered.
        app.bindIfMissing { NotaryConfig(app.env) }
    }
}

/**
 * Return a fully built Notary object for the given name by looking in the NotaryConfig.
 */
fun HttpCall.notary(name: String? = null): Notary {
    val notaryName = (name ?: stringParam("notary")).orAbort()
    return make<NotaryConfig>().notary(this, notaryName).apply {
        build(ServiceBuilder(apiKey).apiSecret(apiSecret).callback(callback))
    }
}
