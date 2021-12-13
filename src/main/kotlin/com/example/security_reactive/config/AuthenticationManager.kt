package com.example.security_reactive.config

import com.example.security_reactive.repository.UserRepository
import com.fasterxml.jackson.databind.JsonSerializer
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthenticationManager @Autowired constructor(var jwtUtil: JwtUtil, var userRepository: UserRepository) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val token = authentication?.credentials.toString()
        return Mono.justOrEmpty(authentication)
            .flatMap { jwt -> mono {  validate(token) } }
            .onErrorMap { error -> IllegalArgumentException(error) }
    }

    private suspend fun validate(token:String): Authentication {
        val userName =jwtUtil.extractUserName(token)
        val user = userRepository.findByEmail(userName).awaitSingleOrNull()
        if (jwtUtil.validateToken(token, user)){
            val authorities : MutableList<SimpleGrantedAuthority> = mutableListOf()
            user?.roles?.forEach {
                val element = it
                authorities.add(SimpleGrantedAuthority(element))
            }
            return UsernamePasswordAuthenticationToken(user?.email, token, authorities)
        }
        throw IllegalArgumentException("Token is not valid!")
    }

}







