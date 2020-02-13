package dev.alpas.notary.twoauth

import dev.alpas.notary.User

class GitHubUser(override val properties: Map<String, Any?>) : User {
    override fun avatar(): String? {
        return properties["avatar_url"]?.toString()
    }

    override fun nickname(): String {
        return properties["login"].toString()
    }
}
