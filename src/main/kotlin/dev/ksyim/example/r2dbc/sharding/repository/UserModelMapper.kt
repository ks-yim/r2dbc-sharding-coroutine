package dev.ksyim.example.r2dbc.sharding.repository

import dev.ksyim.example.r2dbc.sharding.model.UserModel
import dev.ksyim.example.r2dbc.sharding.mysql.ROUTING_KEY
import io.r2dbc.spi.Row
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class UserModelMapper(private val dbClient: DatabaseClient) {
    suspend fun insert(user: UserModel) {
        dbClient
            .sql("INSERT INTO `user`(id, name) VALUES (:id, :name)")
            .bind("id", user.id)
            .bind("name", user.name)
            .then()
            .contextWrite { context -> context.put(ROUTING_KEY, "shard${user.id % 2}") }
            .awaitFirstOrNull()
    }

    suspend fun findById(id: Long) =
        dbClient
            .sql("SELECT u.id AS `u.id`, u.name AS `u.name` FROM `user` u WHERE u.id = :id")
            .bind("id", id)
            .map { row -> UserModel(row.get<Long>("u.id"), row.get<String>("u.name")) }
            .one()
            .contextWrite { context -> context.put(ROUTING_KEY, "shard${id % 2}") }
            .awaitFirstOrNull()
}

private inline fun <reified T> Row.get(name: String) = checkNotNull(this.get(name, T::class.java))