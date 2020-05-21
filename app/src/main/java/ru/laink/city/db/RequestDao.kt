package ru.laink.city.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.laink.city.models.request.Request

@Dao
interface RequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(Request: Request): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(request: List<Request>)

    @Query("SELECT * FROM requests")
    suspend fun getAllRequests(): List<Request>

    @Query("SELECT * FROM requests WHERE type=:status")
    suspend fun getByStatus(status: Int): List<Request>

    @Query("SELECT * FROM requests WHERE authorId=:authorId")
    fun getOwnRequests(authorId: String): LiveData<List<Request>>

    @Delete
    suspend fun deleteRequest(Request: Request)
}