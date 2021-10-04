package dev.ksyim.example.r2dbc.sharding.dto

import dev.ksyim.example.r2dbc.sharding.model.UserModel

data class UserDto(val id: String, val name: String)

fun UserModel.toDto() = UserDto("$id", name)
