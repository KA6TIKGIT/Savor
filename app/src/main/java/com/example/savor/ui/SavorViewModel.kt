package com.example.savor.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.savor.data.local.FavoriteEntity
import com.example.savor.data.local.ReviewEntity
import com.example.savor.data.local.SavorDatabase
import com.example.savor.data.local.SettingsStore
import com.example.savor.data.local.SortOrder
import com.example.savor.data.remote.Product
import com.example.savor.data.remote.RetrofitClient
import com.example.savor.data.repository.SavorRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SavorViewModel(app: Application) : AndroidViewModel(app) {

    private val settings = SettingsStore(app)
    private val repo = SavorRepository(
        SavorDatabase.getInstance(app),
        RetrofitClient.searchApi
    )

    // ---- Search state ----
    val query = MutableStateFlow(settings.lastSearch)
    private val results = MutableStateFlow<List<Product>>(emptyList())
    val loading = MutableStateFlow(false)
    val hasError = MutableStateFlow(false)
    val hasSearched = MutableStateFlow(false)
    private var searchJob: Job? = null

    // ---- Settings state ----
    val sortOrder = MutableStateFlow(settings.sortOrder)
    val darkTheme = MutableStateFlow(settings.darkTheme)

    // Results, sorted according to the saved sort order
    val products: StateFlow<List<Product>> =
        combine(results, sortOrder) { list, sort ->
            when (sort) {
                SortOrder.DEFAULT -> list
                SortOrder.NAME -> list.sortedBy { it.productName?.lowercase() }
                SortOrder.NUTRISCORE -> list.sortedBy { gradeRank(it.nutriscoreGrade) }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ---- Favorites ----
    val favorites: StateFlow<List<Product>> =
        repo.favorites.map { list -> list.map { it.toProduct() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ---- Selected product (detail screen) ----
    val selected = MutableStateFlow<Product?>(null)

    val selectedIsFavorite: StateFlow<Boolean> =
        selected.flatMapLatest { p ->
            p?.code?.let { repo.isFavorite(it) } ?: flowOf(false)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val selectedReviews: StateFlow<List<ReviewEntity>> =
        selected.flatMapLatest { p ->
            p?.code?.let { repo.reviews(it) } ?: flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        if (query.value.isNotBlank()) search(query.value)
    }

    // ---- Actions ----

    fun onQueryChange(text: String) { query.value = text }

    fun search(text: String = query.value) {
        val q = text.trim()
        if (q.isBlank()) return
        settings.lastSearch = q
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            loading.value = true
            hasError.value = false
            try {
                results.value = repo.search(q)
                hasSearched.value = true
            } catch (e: Exception) {
                hasError.value = true
            } finally {
                loading.value = false
            }
        }
    }

    fun select(product: Product) { selected.value = product }

    fun toggleFavorite() {
        val p = selected.value ?: return
        viewModelScope.launch { repo.toggleFavorite(p) }
    }

    fun addReview(rating: Int, text: String) {
        val p = selected.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch { repo.addReview(p, rating, text.trim()) }
    }

    fun deleteReview(review: ReviewEntity) {
        viewModelScope.launch { repo.deleteReview(review) }
    }

    fun setSortOrder(order: SortOrder) {
        settings.sortOrder = order
        sortOrder.value = order
    }

    fun setDarkTheme(enabled: Boolean) {
        settings.darkTheme = enabled
        darkTheme.value = enabled
    }

    // ---- Helpers ----

    // a is best, e is worst, unknown goes last
    private fun gradeRank(grade: String?): Int =
        when (grade?.lowercase()) {
            "a" -> 0; "b" -> 1; "c" -> 2; "d" -> 3; "e" -> 4
            else -> 5
        }

    private fun FavoriteEntity.toProduct() = Product(
        code = code,
        productName = name,
        brands = brands,
        imageUrl = imageUrl,
        imageSmallUrl = imageUrl,
        nutriscoreGrade = nutriscoreGrade
    )
}