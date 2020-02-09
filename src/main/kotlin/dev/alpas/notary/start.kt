package dev.alpas.notary

import dev.alpas.Alpas

fun main(args: Array<String>) = Alpas(args) {
    registerProvider(NotaryServiceProvider())
    routes {
        get() { reply("Welcome to Notary!") }
        get("auth/login") { notary("twitter").redirect() }
        get("auth/twitter/callback") { replyAsJson(notary("twitter").user()) }
    }
}.ignite()

