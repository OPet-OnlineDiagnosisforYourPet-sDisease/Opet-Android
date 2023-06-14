package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class LoginResult(

	@field:SerializedName("profil")
	val profil: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)