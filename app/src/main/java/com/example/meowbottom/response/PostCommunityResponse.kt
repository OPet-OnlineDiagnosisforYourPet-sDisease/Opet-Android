package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class PostCommunityResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)