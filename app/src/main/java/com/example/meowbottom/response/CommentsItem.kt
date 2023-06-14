package com.example.meowbottom.response

import com.google.gson.annotations.SerializedName

data class CommentsItem(

	@field:SerializedName("commenter_name")
	val commenterName: String? = null,

	@field:SerializedName("comment_time")
	val commentTime: String? = null,

	@field:SerializedName("user_email")
	val userEmail: String? = null,

	@field:SerializedName("story_id")
	val storyId: Int? = null,

	@field:SerializedName("comment")
	val comment: String? = null,

	@field:SerializedName("commenter_profil")
	val commenterProfil: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)