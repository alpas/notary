package dev.alpas.notary

import dev.alpas.Config
import dev.alpas.Environment
import dev.alpas.http.HttpCall
import dev.alpas.notary.oneauth.TwitterNotary
import dev.alpas.notary.twoauth.GitHubNotary

typealias NotaryFactory = (call: HttpCall) -> Notary

@Suppress("RemoveExplicitTypeArguments")
open class NotaryConfig(env: Environment) : Config {
    private val notaries = mutableMapOf<String, NotaryFactory>()
    private val defaultNotaries: Map<String, NotaryFactory> by lazy {
        mapOf<String, NotaryFactory>(
            "twitter" to { call -> TwitterNotary(call, env) },
            "github" to { call -> GitHubNotary(call, env, "user") }
        )
    }

    /**
     * Register a new NotaryFactory under the given name.
     *
     * @param name The name to register the given factory under.
     * @param notaryFactory The factory responsible for creating a Notary instance.
     */
    fun addNotary(name: String, notaryFactory: NotaryFactory) {
        notaries[name] = notaryFactory
    }

    /**
     * Return a Notary instance for the given name. It looks in the registered notaries first.
     * If a notary is not found in the registered notaries, it looks in the default
     * notaries. If it is still not found, it throws an IllegalArgumentException.
     *
     * @param call The current HttpCall.
     * @param name The name of the Notary to fetch.
     *
     * @return A Notary object registered under the given name.
     */
    open fun notary(call: HttpCall, name: String): Notary {
        return notaries[name]?.invoke(call)
            ?: defaultNotaries[name]?.invoke(call)
            ?: throw IllegalArgumentException("No Notary provider by the name: '${name}' is registered.")
    }
}
