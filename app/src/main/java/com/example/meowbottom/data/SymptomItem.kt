package com.example.meowbottom.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SymptomItem(
    val id: Int,
    val name: String,
    var isSelected: Boolean = false,
    var value: Int = 0
) : Parcelable
