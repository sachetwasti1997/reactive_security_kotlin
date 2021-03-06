package com.example.security_reactive.service

import com.example.security_reactive.model.LoginRequest
import com.example.security_reactive.model.ResponseProfile
import com.example.security_reactive.model.UserDto
import reactor.core.publisher.Mono

interface UserService {
    fun loginUser(loginRequest: LoginRequest):Mono<ResponseProfile>
    fun saveUser(userDto: UserDto) : Mono<ResponseProfile>
    fun getByEmail(email:String) : Mono<UserDto>
}