package dev.ksyim.example.r2dbc.sharding

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class R2dbcShardingApplication

fun main(args: Array<String>) {
    runApplication<R2dbcShardingApplication>(*args)
}
