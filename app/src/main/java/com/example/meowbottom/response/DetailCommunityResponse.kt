package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class DetailCommunityResponse(

	@field:SerializedName("user_profile")
	val userProfile: String? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("story")
	val story: Story? = null
)