package dev.ksyim.example.r2dbc.sharding.mysql

import io.r2dbc.spi.ConnectionFactoryMetadata
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory
import reactor.core.publisher.Mono

const val ROUTING_KEY: String = "ROUTING_KEY" // better be typed rather than being a magic string.

class RoutingConnectionFactory(
    private val metadata: ConnectionFactoryMetadata
) : AbstractRoutingConnectionFactory() {
    override fun determineCurrentLookupKey(): Mono<Any> =
        Mono.deferContextual { context ->
            when (context.hasKey(ROUTING_KEY)) {
                true -> Mono.just(context.get(ROUTING_KEY))
                false -> Mono.empty()
            }
        }

    override fun getMetadata(): ConnectionFactoryMetadata = metadata
}
