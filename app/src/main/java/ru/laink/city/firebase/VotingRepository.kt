package ru.laink.city.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.laink.city.models.vote.Vote
import ru.laink.city.models.vote.VoteFirebase
import ru.laink.city.util.Constants
import ru.laink.city.util.Constants.Companion.ANSWER_STR
import ru.laink.city.util.Resource
import ru.laink.city.util.voteFirebaseToVote

class VotingRepository(
    private val firestore: CollectionReference = FirebaseFirestore.getInstance()
        .collection(Constants.COLLECTION_VOTING)
) {

    suspend fun getAllVoting(): Resource<List<Vote>, Exception> =
        withContext(Dispatchers.IO) {
            try {

                val document = firestore.get().await()

                Resource.build { resultToVoteList(document) }
            } catch (e: Exception) {
                Resource.build { throw e }
            }
        }

    private fun resultToVoteList(result: QuerySnapshot?): List<Vote> {
        val list = mutableListOf<Vote>()

        result?.forEach { documentSnapshot ->
            val voteFirebase = documentSnapshot.toObject(VoteFirebase::class.java)


            list.add(
                voteFirebaseToVote(
                    documentSnapshot.id,
                    voteFirebase
                )
            )
        }
        return list
    }

    suspend fun getVoteById(id: String): Resource<Vote, Exception> = withContext(Dispatchers.IO) {
        try {

            val voteFirebase =
                firestore.document(id).get().await().toObject(VoteFirebase::class.java)

            Resource.build { voteFirebaseToVote(id, voteFirebase!!) }
        } catch (e: Exception) {
            Resource.build { throw e }
        }
    }

    suspend fun changeAnswer(id: String, answerTitle: String): Resource<Unit, Exception> =
        withContext(Dispatchers.IO) {
            try {
                firestore.document(id).update("${ANSWER_STR}.${answerTitle}", FieldValue.increment(1))

                Resource.build { Unit }
            } catch (e: Exception) {
                Resource.build { throw e }
            }
        }
}