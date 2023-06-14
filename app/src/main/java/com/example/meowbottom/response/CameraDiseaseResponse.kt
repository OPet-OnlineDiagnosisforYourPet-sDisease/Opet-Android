package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class CameraDiseaseResponse(

	@field:SerializedName("Prediction")
	val prediction: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)