package com.example.meowbottom.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryItem(
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    var isSelected: Boolean = false,
    var value: Int = 0
) : Parcelable
