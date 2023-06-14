package com.example.meowbottom.data

data class UserModel(
    val username: String,
    val email: String,
    val token: String,
    val isLogin: Boolean,
    val profil: String? = null
)
