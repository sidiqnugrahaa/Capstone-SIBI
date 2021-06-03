package com.sidiq.sibi.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Alphabet(
    var alphabet : Char,
    var icon : Int,
    var video_url : String
): Parcelable
