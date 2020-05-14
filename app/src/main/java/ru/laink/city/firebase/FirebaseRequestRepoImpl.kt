package ru.laink.city.firebase

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.laink.city.db.RequestDatabase
import ru.laink.city.models.Request
import ru.laink.city.models.RequestRoom
import ru.laink.city.models.User
import ru.laink.city.util.Constants.Companion.COLLECTION_REQUEST
import ru.laink.city.util.Constants.Companion.IMAGE_EXPANSION
import ru.laink.city.util.Resource
import java.io.ByteArrayOutputStream
import java.util.*

class FirebaseRequestRepoImpl(
    val db: RequestDatabase,
    private val firestoreCollection: CollectionReference = FirebaseFirestore.getInstance()
        .collection(COLLECTION_REQUEST),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    private val auth = FirebaseAuth.getInstance()


    suspend fun upsertRequestFirebase(
        request: Request,
        bitmap: Bitmap
    ): Resource<Unit, Exception> =
        withContext(Dispatchers.IO) {
            try {
                val firebaseUser = auth.currentUser
                // Id документа для добавления заявки
                val documentId = UUID.randomUUID()
                // Путь до хранилища картинок определённого пользователя
                val path = "${auth.uid}/$documentId.$IMAGE_EXPANSION"
                // Получение ссылки на хранилище
                val storageReference = storage.getReference(path)
                // Загрузка
                storageReference.putBytes(getPhotoData(bitmap).data!!)

                if (firebaseUser != null) {
                    firestoreCollection.document(documentId.toString()).set(
                        request.copy(
                            author = User(
                                firebaseUser.uid,
                                firebaseUser.displayName ?: "Null"
                            )
                        )
                    ).await()
                }

                Resource.build { Unit }

            } catch (exception: Exception) {
                Resource.build { throw exception }
            }
        }

    private fun getPhotoData(bitmap: Bitmap): Resource<ByteArray, java.lang.Exception> =
            try {
                val outPutStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)

                Resource.build { outPutStream.toByteArray() }
            } catch (exception: java.lang.Exception) {
                Resource.build { throw exception }
            }


    suspend fun upsertRequestDB(request: RequestRoom) {
        db.getRequestDao().upsert(request)
    }
}