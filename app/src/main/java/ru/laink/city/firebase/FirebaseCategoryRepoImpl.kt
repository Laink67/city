package ru.laink.city.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.laink.city.db.RequestDatabase
import ru.laink.city.models.Category
import ru.laink.city.util.Constants.Companion.COLLECTION_CATEGORIES
import ru.laink.city.util.Constants.Companion.COLLECTION_DOCUMENT
import ru.laink.city.util.Resource

class FirebaseCategoryRepoImpl(
    val db: RequestDatabase,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getCategories(): Resource<List<Category>, Exception> {
        return try {
            val categoryList = mutableListOf<Category>()
            val document =
                firestore.collection(COLLECTION_CATEGORIES).document(COLLECTION_DOCUMENT).get()
                    .await()

            document.data?.forEach { data ->
                categoryList.add(Category(data.key.toInt(), data.value.toString()))
                withContext(Dispatchers.IO) {
                    db.getCategoryDao().insert(Category(data.key.toInt(), data.value.toString()))
                }
            }

            Resource.build { categoryList }
        } catch (exception: Exception) {
            Resource.build { throw exception }
        }
    }

    fun getLocalCategory() = db.getCategoryDao().getAll()
}