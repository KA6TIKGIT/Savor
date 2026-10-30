package com.example.savor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.savor.R
import com.example.savor.data.remote.Product

@Composable
fun FavoritesScreen(
    vm: SavorViewModel,
    onOpen: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val favorites by vm.favorites.collectAsState()

    if (favorites.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_favorites))
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(favorites, key = { it.code ?: it.hashCode() }) { product ->
                ProductRow(product) { onOpen(product) }
            }
        }
    }
}
