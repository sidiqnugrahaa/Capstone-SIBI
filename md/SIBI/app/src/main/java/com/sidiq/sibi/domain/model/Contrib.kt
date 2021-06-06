package com.sidiq.sibi.domain.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize


@Parcelize
data class Word(
    @DocumentId
    var word: String = "",
    var link: String = "URL To Video",
    var contrib: Contrib? = null
): Parcelable

@Parcelize
data class Contrib(
    @DocumentId
    var userId: String = "",
    var fileUri: String = "",
    var timestamp: Timestamp? = null,
) : Parcelable{

    companion object {
        fun Contrib.toHistory() : History =
            History(
                timestamp = timestamp,
                type = "Contributing",
                score = 100
            )
    }

}