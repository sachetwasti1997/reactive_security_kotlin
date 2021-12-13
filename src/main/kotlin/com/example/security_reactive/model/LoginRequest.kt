package com.example.security_reactive.model

class LoginRequest{

    private lateinit var email: String
    private lateinit var password: String

    constructor()

    constructor(email:String, password:String){
        this.email = email
        this.password = password
    }

    fun getEmail():String?{
        return email
    }
    fun getPassword():String?{
        return password
    }
}