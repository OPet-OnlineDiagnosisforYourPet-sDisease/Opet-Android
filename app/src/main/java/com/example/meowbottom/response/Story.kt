package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class Story(

	@field:SerializedName("sender_profil")
	val senderProfil: String? = null,

	@field:SerializedName("comment_count")
	val commentCount: Int? = null,

	@field:SerializedName("comments")
	val comments: List<CommentsItem?>? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("photo")
	val photo: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("lon")
	val lon: Double? = null,

	@field:SerializedName("sender_name")
	val senderName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_profile")
	val userProfile: String? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)