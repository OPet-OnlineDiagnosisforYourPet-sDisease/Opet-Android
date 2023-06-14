package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class ArticleResponse(

	@field:SerializedName("artikel")
	val artikel: List<ArticleItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)