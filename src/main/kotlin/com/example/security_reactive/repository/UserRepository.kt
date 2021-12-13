package com.example.security_reactive.repository

import com.example.security_reactive.model.UserDto
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface UserRepository: ReactiveMongoRepository<UserDto, String>{
    fun findByEmail(email: String): Mono<UserDto>
}