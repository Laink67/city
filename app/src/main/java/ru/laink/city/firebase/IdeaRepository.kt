package ru.laink.city.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.laink.city.db.RequestDatabase
import ru.laink.city.models.idea.Idea
import ru.laink.city.util.Constants.Companion.COLLECTION_IDEAS
import ru.laink.city.util.Constants.Companion.IDEAS_STR
import ru.laink.city.util.Resource
import ru.laink.city.util.toIdea
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class IdeaRepository(
    private val db: RequestDatabase,
    private val firestoreCollection: CollectionReference = FirebaseFirestore.getInstance()
        .collection(COLLECTION_IDEAS)
) {

    val auth = FirebaseAuth.getInstance()
    val localIdeas = db.getIdeaDao().getAllByUid(auth.uid!!)

    suspend fun deleteFromDb(id: Long) {
        db.getIdeaDao().deleteById(id)
    }

    private suspend fun insertToDb(idea: Idea): Long {
        return db.getIdeaDao().insert(idea)
    }

    private suspend fun insertAllToDb(ideas: List<Idea>) {
        db.getIdeaDao().insertAll(ideas)
    }

    private fun getByIdAndUid(id: Long, uid: String): Idea {
        return db.getIdeaDao().getById(id, uid)
    }

    suspend fun delete(idea: Idea): Resource<Unit, Exception> = withContext(Dispatchers.IO) {
        try {
            deleteFromDb(idea.id!!)
            idea.userId = auth.currentUser!!.uid

            val document = firestoreCollection.document(idea.userId!!)

            document
                .update(
                    IDEAS_STR, FieldValue.arrayRemove(
                        idea
                    )
                )

            Resource.build { Unit }
        } catch (e: Exception) {
            Resource.build { throw e }
        }
    }

    suspend fun addToFirebaseById(idea: Idea): Resource<Unit, Exception> =
        withContext(Dispatchers.IO) {
            try {
                idea.userId = auth.currentUser!!.uid
                val ideaId = insertToDb(idea)

                val document = firestoreCollection.document(idea.userId!!)
                val documentObject = document.get().await()

                if (!documentObject.exists()) {
                    document.set(mapOf(IDEAS_STR to 0))
                }

                document
                    .update(
                        IDEAS_STR, FieldValue.arrayUnion(
                            getByIdAndUid(
                                ideaId,
                                idea.userId!!
                            )
                        )
                    )


                Resource.build { Unit }
            } catch (e: Exception) {
                Resource.build { throw e }
            }
        }

    suspend fun getAll(): Resource<Unit, Exception> = withContext(Dispatchers.IO) {
        try {

            val document = firestoreCollection.document(auth.uid.toString()).get().await()
            val listOfMaps: ArrayList<HashMap<String, Any>> =
                document.get(IDEAS_STR) as ArrayList<HashMap<String, Any>>

            insertAllToDb(resultToIdea(listOfMaps))
            Resource.build { Unit }
        } catch (e: Exception) {
            Resource.build { throw e }
        }
    }

    private fun resultToIdea(result: ArrayList<HashMap<String, Any>>): List<Idea> {
        val list = mutableListOf<Idea>()

        result.forEach { hashMap ->
            list.add(hashMap.toIdea)
        }
        return list
    }

}