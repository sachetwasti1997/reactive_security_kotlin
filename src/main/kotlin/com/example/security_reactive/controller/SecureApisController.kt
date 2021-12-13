package com.example.security_reactive.controller

import com.example.security_reactive.model.ResponseProfile
import com.example.security_reactive.model.UserDto
import com.example.security_reactive.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
@RequestMapping("/api/v1/auth")
class SecureApisController @Autowired constructor(val userService: UserService) {

    @PostMapping("/subs")
    fun subscribeUser(@RequestBody userDto: UserDto):Mono<ResponseEntity<ResponseProfile>>{
        return userService.saveUser(userDto)
            .map { result -> ResponseEntity<ResponseProfile>(result, HttpStatus.OK) }
    }

    @GetMapping("/find_by_email/{email}")
    fun getUser(@PathVariable email: String):Mono<ResponseEntity<UserDto>>{
        return userService.getByEmail(email)
            .map { user -> ResponseEntity<UserDto>(user, HttpStatus.OK) }
    }

    @GetMapping("/me")
    fun getHello(@AuthenticationPrincipal principal: Principal):Mono<ResponseEntity<String>> = Mono.just(ResponseEntity<String>("Hello There ${principal.name}", HttpStatus.OK))

}
