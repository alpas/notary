package dev.alpas.notary.twoauth

import dev.alpas.notary.User

class GoogleUser(override val properties: Map<String, Any?>) : User {
    override fun id() = properties["sub"].toString()
    override fun avatar() = properties["picture"]?.toString()
    override fun nickname() = properties["name"].toString()
}
