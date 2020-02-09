package dev.alpas.notary

import com.github.scribejava.core.builder.ServiceBuilder
import dev.alpas.Application
import dev.alpas.ServiceProvider
import dev.alpas.http.HttpCall
import dev.alpas.make
import dev.alpas.makeElse

class NotaryServiceProvider : ServiceProvider {
    override fun register(app: Application) {
        // Register the default NotaryConfig if it is not already registered.
        app.makeElse { NotaryConfig(app.env) }
    }
}

/**
 * Return a full built Notary object for the given name searing in the NotaryConfig.
 */
fun HttpCall.notary(name: String): Notary {
    return make<NotaryConfig>().notary(this, name).apply {
        build(ServiceBuilder(apiKey).apiSecret(apiSecret).callback(callback))
    }
}
