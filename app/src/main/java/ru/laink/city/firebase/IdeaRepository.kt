package ru.laink.city.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.laink.city.db.RequestDatabase
import ru.laink.city.models.idea.Idea
import ru.laink.city.util.Constants.Companion.COLLECTION_IDEAS
import ru.laink.city.util.Resource
import java.util.*

class IdeaRepository(
    private val db: RequestDatabase,
    private val firestoreCollection: CollectionReference = FirebaseFirestore.getInstance()
        .collection(COLLECTION_IDEAS)
) {

    val auth = FirebaseAuth.getInstance()
    val localIdeas = db.getIdeaDao().getAll()

    private suspend fun insertToDb(idea: Idea): Long {
        return db.getIdeaDao().insert(idea)
    }

    private fun getByIdAndUid(id: Long, uid: String): Idea {
        return db.getIdeaDao().getById(id, uid)
    }

    suspend fun addToFirebaseById(idea: Idea): Resource<Unit, Exception> =
        withContext(Dispatchers.IO) {
            try {
                idea.userId = auth.currentUser!!.uid
                val ideaId = insertToDb(idea)

                val document = firestoreCollection.document(idea.userId!!)
                val documentObject = document.get().await()

                if (!documentObject.exists()) {
                    document.set(mapOf("ideas" to 0))
                }

                document
                    .update(
                        "ideas", FieldValue.arrayUnion(
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

}