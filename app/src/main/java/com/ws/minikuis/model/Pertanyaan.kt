package com.ws.minikuis.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pertanyaan (
    var id: String = "",
    var petanyaan: String = "",
    var jawabanA: String = "",
    var jawabanB: String = "",
    var jawabanC: String = "",
    var jawabanD: String = ""
): Parcelable