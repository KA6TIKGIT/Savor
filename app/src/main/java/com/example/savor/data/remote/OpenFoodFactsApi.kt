package com.example.savor.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Text search: search.openfoodfacts.org
interface SearchApi {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 24,
        @Query("fields") fields: String = FIELDS
    ): SearchHitsResponse

    companion object {
        const val FIELDS =
            "code,product_name,brands,image_url,image_small_url,nutriscore_grade,categories,ingredients_text"
    }
}

// Product details: world.openfoodfacts.org
interface OpenFoodFactsApi {
    @GET("api/v2/product/{code}.json")
    suspend fun getProduct(
        @Path("code") code: String,
        @Query("fields") fields: String = SearchApi.FIELDS
    ): ProductResponse
}