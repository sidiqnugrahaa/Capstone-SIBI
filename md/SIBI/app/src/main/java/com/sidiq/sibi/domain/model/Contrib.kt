package com.sidiq.sibi.domain.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contrib(
    @DocumentId
    var word: String = "",
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