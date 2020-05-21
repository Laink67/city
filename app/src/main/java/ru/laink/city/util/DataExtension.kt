package ru.laink.city.util

import android.net.Uri
import ru.laink.city.models.request.Request
import ru.laink.city.models.request.RequestFirebase
import ru.laink.city.models.vote.Vote
import ru.laink.city.models.vote.VoteFirebase
import ru.laink.city.util.Constants.Companion.SMOLENSK_LATITUDE
import ru.laink.city.util.Constants.Companion.SMOLENSK_LONGITUDE

internal fun firebaseRequestToRequest(
    id: String,
    requestFirebase: RequestFirebase,
    uri: Uri?
): Request =
    Request(
        id,
        requestFirebase.title ?: "",
        requestFirebase.date ?: "",
        requestFirebase.description ?: "",
        requestFirebase.latitude ?: SMOLENSK_LATITUDE,
        requestFirebase.longitude ?: SMOLENSK_LONGITUDE,
        requestFirebase.authorId ?: "",
        requestFirebase.categoryId ?: 0,
        uri?.toString(),
        requestFirebase.type!!
    )

internal fun voteFirebaseToVote(
    id: String,
    voteFirebase: VoteFirebase
): Vote =
    Vote(
        id,
        voteFirebase.title,
        voteFirebase.description,
        voteFirebase.answer
    )