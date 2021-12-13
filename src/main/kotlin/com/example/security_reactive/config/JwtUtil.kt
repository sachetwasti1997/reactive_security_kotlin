package com.example.security_reactive.config

import com.example.security_reactive.model.UserDto
import com.fasterxml.jackson.databind.AbstractTypeResolver
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import lombok.Data
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function
import kotlin.collections.HashMap
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@Service
class JwtUtil {

    val SECRET_KEY = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    fun extractUserName(token:String):String{
        return extractClaim(token, Claims::getSubject)
    }

    fun extractExpiration(token: String) : Date{
        return extractClaim(token, Claims::getExpiration)
    }

    fun <T>extractClaim(token: String, claimResolver: Function<Claims, T>): T{
        val claims = extractAllClaims(token)
        return claimResolver.apply(claims)
    }

    fun extractAllClaims(token: String):Claims{
        val key = Keys.hmacShaKeyFor(SECRET_KEY.toByteArray())
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun isTokenExpired(token: String):Boolean{
        return extractExpiration(token).before(Date())
    }

    fun generateToken(userDto: UserDto?):String{
        val claims = HashMap<String, Any>()
        return createToken(claims, userDto)
    }

    fun createToken(claims: Map<String, Any>, userDto: UserDto?):String{
        val key = Keys.hmacShaKeyFor(SECRET_KEY.toByteArray())
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDto?.email)
            .claim("roles", userDto?.roles)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 10 * 60 * 60 * 1000))
            .signWith(key)
            .compact()
    }

    fun validateToken(token:String, userDto: UserDto?):Boolean{
        val userName = extractUserName(token)
        return (userName.equals(userDto?.email) && !isTokenExpired(token))
    }

}




