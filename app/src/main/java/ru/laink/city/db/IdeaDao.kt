package ru.laink.city.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.laink.city.models.idea.Idea

@Dao
interface IdeaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(idea: Idea): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ideas: List<Idea>)

    @Query("SELECT * FROM ideas")
    fun getAll(): LiveData<List<Idea>>

    @Query("SELECT * FROM ideas WHERE userId=:uid")
    fun getAllByUid(uid: String): LiveData<List<Idea>>

    @Query("SELECT * FROM ideas WHERE id=:id AND userId=:uid")
    fun getById(id: Long, uid: String): Idea

    @Query("DELETE FROM ideas WHERE id = :id")
    fun deleteById(id: Long)
}