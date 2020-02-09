package dev.alpas.notary.twoauth

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.oauth.OAuth20Service
import dev.alpas.http.HttpCall
import dev.alpas.notary.Notary

abstract class TwoAuthNotary(private val call: HttpCall) : Notary {
    private lateinit var service: OAuth20Service
    protected abstract fun apiService(): DefaultApi20

    override fun build(builder: ServiceBuilder) {
        service = builder.build(apiService())
    }
}
