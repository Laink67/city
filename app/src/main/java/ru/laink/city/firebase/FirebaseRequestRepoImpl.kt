package ru.laink.city.firebase

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.laink.city.db.RequestDatabase
import ru.laink.city.models.request.Request
import ru.laink.city.models.request.RequestFirebase
import ru.laink.city.util.Constants.Companion.COLLECTION_REQUEST
import ru.laink.city.util.Constants.Companion.IMAGE_EXPANSION
import ru.laink.city.util.Constants.Companion.STATUS_ALL
import ru.laink.city.util.Resource
import ru.laink.city.util.firebaseRequestToRequest
import ru.laink.city.util.requestToFirebaseRequest
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class FirebaseRequestRepoImpl(
    private val db: RequestDatabase,
    private val firestoreCollection: CollectionReference = FirebaseFirestore.getInstance()
        .collection(COLLECTION_REQUEST),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    private val auth = FirebaseAuth.getInstance()

    val localOwnRequests = db.getRequestDao().getOwnRequests(auth.currentUser!!.uid)

    suspend fun getAllRequests(): Resource<List<Request>, Exception> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = firestoreCollection.get().await()

            val requests = resultToRequestList(querySnapshot)

            insertAllToDb(requests)

            Resource.build { requests }
        } catch (exception: Exception) {
            Resource.build { throw exception }
        }
    }

    suspend fun deleteRequest(request: Request): Resource<Unit, Exception> =
        withContext(Dispatchers.IO) {
            try {
                val documents = firestoreCollection
                    .whereEqualTo("description", request.description).get().await()

                documents.forEach { document ->
                    document.reference.delete()
                }
                // Удаление локальной записи
                deleteFromDb(request)

                Resource.build { Unit }
            } catch (e: Exception) {
                Resource.build { throw e }
            }
        }

    suspend fun undoRequest(request: Request): Resource<Unit, Exception> =
        withContext(Dispatchers.IO) {
            try {

                val firebaseUser = auth.currentUser
                // Id документа для добавления заявки
                val documentId = UUID.randomUUID()
                val requestFirebase = requestToFirebaseRequest(request)

                if (firebaseUser != null) {
                    firestoreCollection.document(documentId.toString()).set(
                        requestFirebase
                    ).await()
                }

                insertToDb(request)

                Resource.build { Unit }
            } catch (e: Exception) {
                Resource.build { throw e }
            }
        }

    suspend fun upsertRequestFirebase(
        requestFirebase: RequestFirebase,
        bitmap: Bitmap,
        documentId: String?
    ): Resource<Unit, Exception> =
        withContext(Dispatchers.IO) {
            try {

                val firebaseUser = auth.currentUser
                // Id документа для добавления заявки
                val docId =
                    documentId ?: UUID.randomUUID().toString()
                // Путь до хранилища картинок определённого пользователя
                val path = "${auth.uid}/$docId.$IMAGE_EXPANSION"
                // Получение ссылки на хранилище
                val storageReference = storage.getReference(path)
                // Загрузка
                storageReference.putBytes(getPhotoDataFromBitmap(bitmap).data!!)

                if (firebaseUser != null) {
                    firestoreCollection.document(docId).set(
                        requestFirebase.copy(
                            authorId =
                            firebaseUser.uid
                        )
                    ).await()
                }

                Resource.build { Unit }

            } catch (exception: Exception) {
                Resource.build { throw exception }
            }
        }

    private fun getPhotoDataFromBitmap(bitmap: Bitmap): Resource<ByteArray, java.lang.Exception> =
        try {
            val outPutStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)

            Resource.build { outPutStream.toByteArray() }
        } catch (exception: java.lang.Exception) {
            Resource.build { throw exception }
        }


    suspend fun getUserRequests(): Resource<List<Request>, java.lang.Exception> =
        withContext(Dispatchers.IO) {
            try {
                val currentUser = auth.currentUser!!

                val document =
                    firestoreCollection.whereEqualTo(
                        "authorId",
                        currentUser.uid
                    ).get().await()

                Resource.build { resultToRequestList(document) }
            } catch (exception: IOException) {
                Resource.build { throw exception }
            }
        }

    private suspend fun resultToRequestList(result: QuerySnapshot?): List<Request> {

        val list = mutableListOf<Request>()

        result?.forEach { documentSnapshot ->

            val requestFirebase = documentSnapshot.toObject(RequestFirebase::class.java)

            // Получение ссылки на хранилище
            val storageReference: Uri? = try {
                storage.getReference(
                    "${requestFirebase.authorId}/${documentSnapshot.id}.$IMAGE_EXPANSION"
                ).downloadUrl.await()
            } catch (e: Exception) {
                null
            }

            list.add(
                firebaseRequestToRequest(
                    documentSnapshot.id,
                    requestFirebase,
                    storageReference
                )
            )
        }

        return list
    }

    suspend fun insertAllToDb(requests: List<Request>) {
        db.getRequestDao().insertAll(requests)
    }

    private suspend fun insertToDb(request: Request) {
        db.getRequestDao().upsert(request)
    }

    private suspend fun deleteFromDb(request: Request) {
        db.getRequestDao().deleteRequest(request)
    }

    suspend fun getByStatus(status: Int) =
        if (status != STATUS_ALL) {
            db.getRequestDao().getByStatus(status)
        } else {
            db.getRequestDao().getAllRequests()
        }
}