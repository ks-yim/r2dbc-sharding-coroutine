package dev.ksyim.example.r2dbc.sharding.service

import com.linecorp.armeria.server.annotation.ConsumesJson
import com.linecorp.armeria.server.annotation.Get
import com.linecorp.armeria.server.annotation.Param
import com.linecorp.armeria.server.annotation.PathPrefix
import com.linecorp.armeria.server.annotation.Post
import com.linecorp.armeria.server.annotation.ProducesJson
import dev.ksyim.example.r2dbc.sharding.dto.CreateUserDto
import dev.ksyim.example.r2dbc.sharding.dto.UserDto
import dev.ksyim.example.r2dbc.sharding.dto.toDto
import dev.ksyim.example.r2dbc.sharding.model.UserModel
import dev.ksyim.example.r2dbc.sharding.repository.UserModelMapper
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong

@Component
@PathPrefix("/users")
@ProducesJson
@ConsumesJson
class UserService(private val userModelMapper: UserModelMapper) {
    @Post
    suspend fun createUser(user: CreateUserDto) {
        userModelMapper.insert(UserModel(nextUserId(), user.name))
    }

    @Get("/{userId}")
    suspend fun getUserByUserId(@Param userId: Long): UserDto =
        userModelMapper.findById(userId)?.toDto() ?: throw RuntimeException("not found")
}

private val userIdCounter = AtomicLong()

private fun nextUserId() = userIdCounter.incrementAndGet()