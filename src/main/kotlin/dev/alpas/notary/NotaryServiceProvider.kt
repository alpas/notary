package dev.alpas.notary

import com.github.scribejava.core.builder.ServiceBuilder
import dev.alpas.Application
import dev.alpas.ServiceProvider
import dev.alpas.bindIfMissing
import dev.alpas.http.HttpCall
import dev.alpas.make

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
    val notaryName = name ?: string("notary")
    return make<NotaryConfig>().notary(this, notaryName).apply {
        build(ServiceBuilder(apiKey).apiSecret(apiSecret).callback(callback))
    }
}
