package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class CommunityResponse(

	@field:SerializedName("stories")
	val stories: List<StoriesItem>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)