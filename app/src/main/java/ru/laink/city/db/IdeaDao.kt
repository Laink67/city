package ru.laink.city.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.laink.city.models.idea.Idea

@Dao
interface IdeaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(idea: Idea): Long

    @Query("SELECT * FROM ideas")
    fun getAll(): LiveData<List<Idea>>

    @Query("SELECT * FROM ideas WHERE id=:id AND userId=:uid")
    fun getById(id: Long, uid: String): Idea
}