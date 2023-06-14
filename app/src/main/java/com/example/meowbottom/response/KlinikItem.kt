package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class KlinikItem(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("lon")
	val lon: String? = null,

	@field:SerializedName("lat")
	val lat: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null
)