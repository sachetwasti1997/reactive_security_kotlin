package com.example.security_reactive.service

import com.example.security_reactive.config.JwtUtil
import com.example.security_reactive.model.ResponseProfile
import com.example.security_reactive.model.UserDto
import com.example.security_reactive.repository.UserRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import javax.naming.CannotProceedException

@Service
class UserServiceImpl @Autowired constructor(
    val userRepository: UserRepository,
    val jwtUtil: JwtUtil
) : UserService {

    override fun saveUser(userDto: UserDto): Mono<ResponseProfile> {
        return mono { save(userDto) }
    }

    override fun getByEmail(email: String): Mono<UserDto> {
        return userRepository.findByEmail(email)
    }

    private suspend fun save(userDto: UserDto): ResponseProfile{
        val user = userRepository.save(userDto).awaitSingleOrNull()
        return ResponseProfile(jwtUtil.generateToken(user))
    }
}


