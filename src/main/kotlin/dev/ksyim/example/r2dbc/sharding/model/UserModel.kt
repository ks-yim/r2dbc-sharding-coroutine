package dev.ksyim.example.r2dbc.sharding.model

import com.fasterxml.jackson.annotation.JsonProperty

data class UserModel(
    @JsonProperty("id") val id: Long,
    @JsonProperty("name") val name: String
)
