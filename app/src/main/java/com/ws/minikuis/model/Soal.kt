package com.ws.minikuis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Soal (
    var pertanyaan: String,
    var a: String,
    var b: String,
    var c: String,
    var d: String,
    var jawaban_tepat: String,
    var status: String
): Parcelable