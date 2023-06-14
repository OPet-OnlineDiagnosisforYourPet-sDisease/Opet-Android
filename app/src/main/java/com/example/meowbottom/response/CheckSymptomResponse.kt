package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class CheckSymptomResponse(

	@field:SerializedName("Prediction")
	val prediction: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("gejala")
	val gejala: String? = null
)