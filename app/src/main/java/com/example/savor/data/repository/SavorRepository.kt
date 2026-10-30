package com.example.savor.data.repository

import com.example.savor.data.local.FavoriteEntity
import com.example.savor.data.local.ReviewEntity
import com.example.savor.data.local.SavorDatabase
import com.example.savor.data.remote.OpenFoodFactsApi
import com.example.savor.data.remote.Product
import com.example.savor.data.remote.RetrofitClient
import com.example.savor.data.remote.SearchApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

class SavorRepository(
    db: SavorDatabase,
    private val searchApi: SearchApi = RetrofitClient.searchApi,
    private val productApi: OpenFoodFactsApi = RetrofitClient.api
) {
    private val favoriteDao = db.favoriteDao()
    private val reviewDao = db.reviewDao()

    // ---- Remote (Retrofit) ----

    // Retries up to 2 times on a 503 or network hiccup, since Open Food Facts rate-limits.
    suspend fun search(query: String): List<Product> {
        var attempt = 0
        while (true) {
            try {
                return searchApi.search(query).hits.orEmpty()
                    .map { it.toProduct() }
                    .filter { !it.code.isNullOrBlank() && !it.productName.isNullOrBlank() }
            } catch (e: HttpException) {
                if (e.code() != 503 || attempt >= 2) throw e
            } catch (e: IOException) {
                if (attempt >= 2) throw e
            }
            attempt++
            delay(1500L * attempt)
        }
    }

    suspend fun getProduct(code: String): Product? {
        return try {
            val response = productApi.getProduct(code)
            if (response.status == 1) response.product?.sanitized() else null
        } catch (_: Exception) {
            null
        }
    }

    // ---- Favorites (Room) ----

    val favorites: Flow<List<FavoriteEntity>> = favoriteDao.getAll()

    fun isFavorite(code: String): Flow<Boolean> = favoriteDao.isFavorite(code)

    suspend fun toggleFavorite(product: Product) {
        val code = product.code ?: return
        if (favoriteDao.isFavorite(code).first()) {
            favoriteDao.deleteByCode(code)
        } else {
            favoriteDao.insert(
                FavoriteEntity(
                    code = code,
                    name = product.productName,
                    brands = product.brands,
                    imageUrl = product.imageUrl,
                    nutriscoreGrade = product.nutriscoreGrade,
                    categories = product.categories,
                    ingredientsText = product.ingredientsText
                )
            )
        }
    }

    // ---- Reviews (Room) ----

    fun reviews(code: String): Flow<List<ReviewEntity>> = reviewDao.getForProduct(code)

    suspend fun addReview(product: Product, rating: Int, text: String) {
        val code = product.code ?: return
        reviewDao.insert(
            ReviewEntity(
                productCode = code,
                productName = product.productName,
                rating = rating,
                text = text
            )
        )
    }

    suspend fun deleteReview(review: ReviewEntity) = reviewDao.delete(review)
}
