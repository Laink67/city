package ru.laink.city.util

import android.net.Uri
import ru.laink.city.models.idea.Idea
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

internal fun requestToFirebaseRequest(request: Request): RequestFirebase =
    RequestFirebase(
        title = request.title,
        date = request.date,
        description = request.description,
        latitude = request.latitude,
        longitude = request.longitude,
        authorId = request.authorId,
        categoryId = request.categoryId,
        type = request.type
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

internal val HashMap<String, Any>.toIdea: Idea
    get() = Idea(
        id = this["id"] as Long,
        title = this["title"].toString(),
        description = this["description"].toString(),
        status = (this["status"] as Long).toInt(),
        userId = this["userId"].toString()
    )

