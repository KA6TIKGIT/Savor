package com.example.savor.data.remote

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

data class SearchResponse(
    val count: Int? = null,
    val page: Int? = null,
    val products: List<Product>? = null
)

data class ProductResponse(
    val status: Int? = null,
    val product: Product? = null
)

data class Product(
    val code: String? = null,
    @SerializedName("product_name") val productName: String? = null,
    val brands: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("image_small_url") val imageSmallUrl: String? = null,
    @SerializedName("nutriscore_grade") val nutriscoreGrade: String? = null,
    val categories: String? = null,
    @SerializedName("ingredients_text") val ingredientsText: String? = null
) {
    fun sanitized(): Product = copy(
        code = code.cleanText(),
        productName = productName.cleanText(),
        brands = brands.cleanText(),
        imageUrl = imageUrl.cleanText(),
        imageSmallUrl = (imageSmallUrl ?: imageUrl).cleanText(),
        nutriscoreGrade = nutriscoreGrade.cleanText()?.lowercase(),
        categories = categories.cleanText(),
        ingredientsText = ingredientsText.cleanText()
    )
}

// Search-a-licious response. Some fields can be a string OR a list,
// so we read them as JsonElement and convert ourselves.
data class SearchHitsResponse(
    val hits: List<Hit>? = null,
    val count: Int? = null
)

data class Hit(
    val code: String? = null,
    @SerializedName("product_name") val productName: JsonElement? = null,
    val brands: JsonElement? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("image_small_url") val imageSmallUrl: String? = null,
    @SerializedName("nutriscore_grade") val nutriscoreGrade: String? = null,
    val categories: JsonElement? = null,
    @SerializedName("ingredients_text") val ingredientsText: JsonElement? = null
) {
    fun toProduct() = Product(
        code = code.cleanText(),
        productName = productName.asText().cleanText(),
        brands = brands.asText().cleanText(),
        imageUrl = imageUrl.cleanText(),
        imageSmallUrl = (imageSmallUrl ?: imageUrl).cleanText(),
        nutriscoreGrade = nutriscoreGrade.cleanText()?.lowercase(),
        categories = categories.asText().cleanText(),
        ingredientsText = ingredientsText.asText().cleanText()
    )
}

fun JsonElement?.asText(): String? = when {
    this == null || isJsonNull -> null
    isJsonArray -> {
        val items = asJsonArray.mapNotNull { elem ->
            when {
                elem == null || elem.isJsonNull -> null
                elem.isJsonPrimitive -> elem.asString
                else -> null
            }
        }.filter { it.isNotBlank() }
        if (items.isEmpty()) null else items.joinToString(", ")
    }
    isJsonPrimitive -> asString
    else -> null
}

fun String?.cleanText(): String? {
    if (this == null) return null
    val trimmed = trim()
    if (trimmed.isEmpty() ||
        trimmed.equals("null", ignoreCase = true) ||
        trimmed.equals("unknown", ignoreCase = true)
    ) {
        return null
    }
    return trimmed
}
