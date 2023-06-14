package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class ArticleItem(

	@field:SerializedName("penulis")
	val penulis: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("judul")
	val judul: String? = null,

	@field:SerializedName("gambar")
	val gambar: String? = null
)