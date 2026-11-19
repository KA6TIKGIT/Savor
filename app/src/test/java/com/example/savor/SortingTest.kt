package com.example.savor

import com.example.savor.data.local.SortOrder
import com.example.savor.data.remote.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class SortingTest {

    private fun gradeRank(grade: String?): Int =
        when (grade?.lowercase()) {
            "a" -> 0
            "b" -> 1
            "c" -> 2
            "d" -> 3
            "e" -> 4
            else -> 5
        }

    private fun sortProducts(list: List<Product>, order: SortOrder): List<Product> =
        when (order) {
            SortOrder.DEFAULT -> list
            SortOrder.NAME -> list.sortedBy { it.productName?.lowercase() }
            SortOrder.NUTRISCORE -> list.sortedBy { gradeRank(it.nutriscoreGrade) }
        }

    @Test
    fun testNutriScoreRank() {
        assertEquals(0, gradeRank("a"))
        assertEquals(0, gradeRank("A"))
        assertEquals(1, gradeRank("b"))
        assertEquals(2, gradeRank("c"))
        assertEquals(3, gradeRank("d"))
        assertEquals(4, gradeRank("e"))
        assertEquals(5, gradeRank("unknown"))
        assertEquals(5, gradeRank(null))
        assertEquals(5, gradeRank("f"))
    }

    @Test
    fun testSortByName() {
        val p1 = Product(code = "1", productName = "Banana")
        val p2 = Product(code = "2", productName = "apple")
        val p3 = Product(code = "3", productName = "Cherry")

        val sorted = sortProducts(listOf(p1, p2, p3), SortOrder.NAME)
        assertEquals(listOf("2", "1", "3"), sorted.map { it.code })
    }

    @Test
    fun testSortByNutriScore() {
        val p1 = Product(code = "1", nutriscoreGrade = "d")
        val p2 = Product(code = "2", nutriscoreGrade = "a")
        val p3 = Product(code = "3", nutriscoreGrade = null)
        val p4 = Product(code = "4", nutriscoreGrade = "B")

        val sorted = sortProducts(listOf(p1, p2, p3, p4), SortOrder.NUTRISCORE)
        assertEquals(listOf("2", "4", "1", "3"), sorted.map { it.code })
    }
}
