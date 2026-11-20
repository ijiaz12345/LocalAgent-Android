package com.localagent.data.media

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY createdAt DESC")
    fun getAllMedia(): Flow<List<MediaEntity>>

    @Insert
    suspend fun insertMedia(item: MediaEntity)

    @Query("DELETE FROM media_items WHERE id = :id")
    suspend fun deleteMedia(id: String)
}
