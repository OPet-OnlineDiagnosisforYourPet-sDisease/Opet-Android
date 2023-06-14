package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class MapClinicResponse(

	@field:SerializedName("klinik")
	val klinik: List<KlinikItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)