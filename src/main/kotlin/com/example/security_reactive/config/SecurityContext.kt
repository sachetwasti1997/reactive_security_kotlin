package com.example.security_reactive.config

import com.example.security_reactive.repository.UserRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecurityContext @Autowired constructor(
    var authenticationManager: AuthenticationManager,
    var jwtUtil: JwtUtil,
    var userRepository: UserRepository
):
    ServerSecurityContextRepository {

    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> {
        return Mono.empty()
    }

    override fun load(exchange: ServerWebExchange?): Mono<SecurityContext> {
        var bearer = "Bearer "
        return Mono.justOrEmpty(exchange?.request?.headers?.getFirst(HttpHeaders.AUTHORIZATION))
                .filter {
                    it.startsWith(bearer)
                }
                .map {
                    it.substring(bearer.length)
                }
                .flatMap {
                    jwt -> mono {  returnAuth(jwt) }
                }
                .flatMap{
                    auth -> authenticationManager.authenticate(auth)
                    .map {
                        SecurityContextImpl(it)
                    }
                }
    }

    private suspend fun returnAuth(token: String): UsernamePasswordAuthenticationToken{
        val email = jwtUtil.extractUserName(token)
        var user = userRepository.findByEmail(email).awaitSingleOrNull()
        var authorities: MutableList<SimpleGrantedAuthority> = mutableListOf()
        user?.roles?.forEach{
            authorities.add(SimpleGrantedAuthority(it))
        }
        return UsernamePasswordAuthenticationToken(
            user?.email,
            token,
            authorities
        )
    }

}









