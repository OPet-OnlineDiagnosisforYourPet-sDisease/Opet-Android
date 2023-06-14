package com.example.meowbottom.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryItem(
    val disease: String,
    val date: String
) : Parcelable
