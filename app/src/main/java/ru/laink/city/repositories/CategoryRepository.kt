package ru.laink.city.repositories

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.FirebaseCategoryRepoImpl
import ru.laink.city.models.Category
import ru.laink.city.util.Resource
import java.io.IOException

class CategoryRepository(
    private val db: RequestDatabase,
    private val firebaseCategoryRepoImpl: FirebaseCategoryRepoImpl
) {
    val categories: LiveData<List<Category>> = db.getCategoryDao().getAll()

/*
    suspend fun getAllCategories(): Resource<List<Category>?, Exception> {
        return db.getCategoryDao().getAll().value
    }
*/

    suspend fun refreshCategories() {
//        withContext(Dispatchers.IO) {
//
//            firebaseCategoryRepoImpl.getCategories().data?.let { categories ->
//                db.getCategoryDao().insertAll(categories)
//            }
//

            when (val categoriesResource = firebaseCategoryRepoImpl.getCategories()) {
                is Resource.Success -> {
                    withContext(Dispatchers.IO) {
                        db.getCategoryDao().insertAll(categoriesResource.data!!)
                    }
                }
                is Resource.Error -> {
                    throw IOException("${categoriesResource.message}")
                }
            }
        }
}