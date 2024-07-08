package com.example.snapdump

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Entity(
    val id: Int,
    val title: String,
    val lat: Double,
    val lon: Double,
    val image: String
) : Parcelable
