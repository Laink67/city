package ru.laink.city.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.laink.city.models.Request

@Dao
interface RequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(request: Request):Long

    @Query("SELECT * FROM requests")
    fun getAllRequests():LiveData<List<Request>>

    @Delete
    suspend fun deleteRequest(request: Request)
}