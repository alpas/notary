# Notary

Notary is the social authentication provider for [Alpas](https://alpas.dev).

Currently, only Twitter authentication is supported but we are working on adding more providers.

Here is an example of using Notary Twitter Authentication in an Alpas app:

```kotlin

fun main(args: Array<String>) = Alpas(args) {
    registerProvider(NotaryServiceProvider())
    routes {
        get() { reply("Welcome to Notary!") }
        get("auth/login") { notary("twitter").redirect() }
        get("auth/twitter/callback") { replyAsJson(notary("twitter").user()) }
    }
}.ignite()

```

You need three variables in your `.env` file:

```

TWITTER_API_KEY=
TWITTER_API_SECRET=
TWITTER_API_CALLBACK=http://localhost:8080/auth/twitter/callback

```
