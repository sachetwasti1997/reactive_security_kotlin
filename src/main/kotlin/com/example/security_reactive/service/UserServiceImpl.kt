package com.example.security_reactive.service

import com.example.security_reactive.config.JwtUtil
import com.example.security_reactive.model.LoginRequest
import com.example.security_reactive.model.ResponseProfile
import com.example.security_reactive.model.UserDto
import com.example.security_reactive.repository.UserRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.nio.file.attribute.UserPrincipalNotFoundException
import javax.naming.CannotProceedException

@Service
class UserServiceImpl @Autowired constructor(
    val userRepository: UserRepository,
    val jwtUtil: JwtUtil,
    val bCryptPasswordEncoder: BCryptPasswordEncoder
) : UserService {

    override fun loginUser(loginRequest: LoginRequest): Mono<ResponseProfile> {
        return mono { loginSearch(loginRequest) }
    }

    override fun saveUser(userDto: UserDto): Mono<ResponseProfile> {
        return mono { save(userDto) }
    }

    override fun getByEmail(email: String): Mono<UserDto> {
        return userRepository.findByEmail(email)
    }

    private suspend fun save(userDto: UserDto): ResponseProfile{
        userDto.password = bCryptPasswordEncoder.encode(userDto.password)
        val user = userRepository.save(userDto).awaitSingleOrNull()
        return ResponseProfile(jwtUtil.generateToken(user))
    }

    private suspend fun loginSearch(loginRequest: LoginRequest): ResponseProfile{
        val user = loginRequest.getEmail()?.let { userRepository.findByEmail(it).awaitSingleOrNull() }
        if (user != null) {
            var passMatch = bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.password)
            if (!passMatch){
                throw UserPrincipalNotFoundException("No User Found")
            }else{
                return ResponseProfile(jwtUtil.generateToken(user))
            }
        }
        throw UserPrincipalNotFoundException("No User Found")
    }

}





