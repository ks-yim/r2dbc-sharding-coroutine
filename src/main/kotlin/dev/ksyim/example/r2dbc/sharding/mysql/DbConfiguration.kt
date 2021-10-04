package dev.ksyim.example.r2dbc.sharding.mysql

import io.r2dbc.pool.PoolingConnectionFactoryProvider
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ConnectionFactoryProps::class)
class DbConfiguration {
    @Bean
    fun shardedDbConnectionFactory(props: ConnectionFactoryProps): ConnectionFactory {
        val shards: Map<String, ConnectionFactory> = props.conn.mapValues {
            val prop = it.value
            val optionBuilder = ConnectionFactoryOptions
                .parse(prop.url)
                .mutate()
                .option(ConnectionFactoryOptions.USER, prop.username)
                .option(ConnectionFactoryOptions.PASSWORD, prop.password)
                .option(PoolingConnectionFactoryProvider.INITIAL_SIZE, prop.pool.initialSize)
                .option(PoolingConnectionFactoryProvider.MAX_SIZE, prop.pool.maxSize)

            ConnectionFactoryBuilder.withOptions(optionBuilder).build()
        }
        val metadata = shards.values.first().metadata

        return RoutingConnectionFactory(metadata).apply {
            setTargetConnectionFactories(shards)
        }
    }
}
