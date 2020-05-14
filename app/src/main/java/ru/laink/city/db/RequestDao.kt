package ru.laink.city.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.laink.city.models.RequestRoom

@Dao
interface RequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(requestRoom: RequestRoom):Long

    @Query("SELECT * FROM requests")
    fun getAllRequests():LiveData<List<RequestRoom>>

    @Delete
    suspend fun deleteRequest(requestRoom: RequestRoom)
}