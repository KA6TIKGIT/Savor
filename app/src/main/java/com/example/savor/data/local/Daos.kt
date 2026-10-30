package com.example.savor.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE code = :code)")
    fun isFavorite(code: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE code = :code")
    suspend fun deleteByCode(code: String)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE productCode = :code ORDER BY createdAt DESC")
    fun getForProduct(code: String): Flow<List<ReviewEntity>>

    @Insert
    suspend fun insert(review: ReviewEntity)

    @Delete
    suspend fun delete(review: ReviewEntity)
}