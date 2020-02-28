package com.ws.minikuis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    var name: String = "",
    var skor: Int = 0
): Parcelable