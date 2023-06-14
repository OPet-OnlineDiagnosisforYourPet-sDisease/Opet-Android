package com.example.meowbottom.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommunityItem(
    val id: Int,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val photoUser: String
) : Parcelable
