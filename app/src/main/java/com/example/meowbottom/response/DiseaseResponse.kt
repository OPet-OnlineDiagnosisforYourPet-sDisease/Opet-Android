package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName


data class DiseaseResponse(

	@field:SerializedName("deskripsi_penyakit")
	val deskripsiPenyakit: List<String?>? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("penanganan")
	val penanganan: List<String?>? = null
)