package com.example.savor

import com.example.savor.data.remote.Hit
import com.example.savor.data.remote.Product
import com.example.savor.data.remote.asText
import com.example.savor.data.remote.cleanText
import com.google.gson.JsonArray
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ModelsTest {

    @Test
    fun testCleanText() {
        assertNull(null.cleanText())
        assertNull("".cleanText())
        assertNull("   ".cleanText())
        assertNull("null".cleanText())
        assertNull("NULL".cleanText())
        assertNull("unknown".cleanText())
        assertNull("UNKNOWN".cleanText())
        assertEquals("Pizza Margherita", "  Pizza Margherita  ".cleanText())
    }

    @Test
    fun testAsText_primitive() {
        val prim = JsonPrimitive("Organic Pasta")
        assertEquals("Organic Pasta", prim.asText())
    }

    @Test
    fun testAsText_null() {
        assertNull(JsonNull.INSTANCE.asText())
        assertNull((null as com.google.gson.JsonElement?).asText())
    }

    @Test
    fun testAsText_array() {
        val array = JsonArray().apply {
            add("Snacks")
            add("Sweet snacks")
            add("")
        }
        assertEquals("Snacks, Sweet snacks", array.asText())
    }

    @Test
    fun testHit_toProduct_sanitization() {
        val hit = Hit(
            code = "123456",
            productName = JsonPrimitive("Dark Chocolate"),
            brands = JsonPrimitive("null"),
            imageUrl = "https://images.openfoodfacts.org/front.jpg",
            imageSmallUrl = null,
            nutriscoreGrade = "B",
            categories = JsonPrimitive("unknown"),
            ingredientsText = JsonPrimitive("Cocoa, Sugar")
        )

        val product = hit.toProduct()

        assertEquals("123456", product.code)
        assertEquals("Dark Chocolate", product.productName)
        assertNull(product.brands) // "null" filtered out
        assertEquals("https://images.openfoodfacts.org/front.jpg", product.imageUrl)
        assertEquals("https://images.openfoodfacts.org/front.jpg", product.imageSmallUrl) // falls back to imageUrl
        assertEquals("b", product.nutriscoreGrade) // normalized to lowercase
        assertNull(product.categories) // "unknown" filtered out
        assertEquals("Cocoa, Sugar", product.ingredientsText)
    }

    @Test
    fun testProduct_sanitized() {
        val raw = Product(
            code = "999",
            productName = "  Cookies  ",
            brands = "NULL",
            imageUrl = "https://img.jpg",
            imageSmallUrl = null,
            nutriscoreGrade = "E",
            categories = "unknown",
            ingredientsText = "Wheat flour"
        )
        val clean = raw.sanitized()

        assertEquals("Cookies", clean.productName)
        assertNull(clean.brands)
        assertNull(clean.categories)
        assertEquals("e", clean.nutriscoreGrade)
        assertEquals("https://img.jpg", clean.imageSmallUrl)
    }
}
