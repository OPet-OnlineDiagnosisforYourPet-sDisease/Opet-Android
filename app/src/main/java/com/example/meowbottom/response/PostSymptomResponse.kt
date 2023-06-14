package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class PostSymptomResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("gejala")
	val gejala: List<String?>? = null
)