package com.example.meowbottom.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemArticle(
    val author: String,
    val title: String,
    val description: String,
    val date: String,
    val photo: String,
) : Parcelable
