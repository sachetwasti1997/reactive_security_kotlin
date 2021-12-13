package com.example.security_reactive.model

import lombok.Data
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Data
@Document(collection = "user")
data class UserDto(
    @Id
    var id: String? = null,
    var email: String? = null,
    var password: String? = null,
    var roles: List<String> ?= null
)