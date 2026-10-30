package com.example.savor.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// A favorited product. We store a snapshot so favorites work offline.
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val code: String,
    val name: String?,
    val brands: String?,
    val imageUrl: String?,
    val nutriscoreGrade: String?,
    val addedAt: Long = System.currentTimeMillis()
)

// A personal review written by the user for a product.
@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productCode: String,
    val productName: String?,
    val rating: Int,          // 1 to 5 stars
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)