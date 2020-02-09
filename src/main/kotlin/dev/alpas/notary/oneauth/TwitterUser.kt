package dev.alpas.notary.oneauth

import dev.alpas.notary.User

open class TwitterUser(override val properties: Map<String, Any?>) : User {
    override fun avatar(): String? {
        return properties["profile_image_url_https"]?.toString()
    }

    override fun nickname(): String {
        return properties["screen_name"].toString()
    }
}
