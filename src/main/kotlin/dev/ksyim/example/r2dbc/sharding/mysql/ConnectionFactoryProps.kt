package dev.ksyim.example.r2dbc.sharding.mysql

import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("mysql.shard")
data class ConnectionFactoryProps(val conn: Map<String, R2dbcProperties>)
