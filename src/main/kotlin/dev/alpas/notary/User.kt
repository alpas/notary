package dev.alpas.notary

import dev.alpas.JsonSerializable
import dev.alpas.JsonSerializer

interface User : JsonSerializable {
    val properties: Map<String, Any?>
    fun id(): String = properties["id"].toString()
    fun name(): String = properties["name"].toString()
    fun email(): String? = properties["email"]?.toString()
    fun nickname(): String
    fun avatar(): String?

    override fun toJson() = JsonSerializer.serialize(properties)
}
