package com.example.savor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.savor.R
import com.example.savor.data.remote.Product

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    vm: SavorViewModel,
    onSearch: (String) -> Unit,
    onOpen: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val favorites by vm.favorites.collectAsState()
    val lastSearch by vm.query.collectAsState()
    val popular = stringArrayResource(R.array.popular_searches)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.screen_padding)),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column {
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (lastSearch.isNotBlank()) {
            Button(
                onClick = { onSearch(lastSearch) },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.continue_search, lastSearch)) }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.popular_searches_title),
                style = MaterialTheme.typography.titleMedium
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                popular.forEach { term ->
                    SuggestionChip(onClick = { onSearch(term) }, label = { Text(term) })
                }
            }
        }

        if (favorites.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(R.string.your_favorites_title),
                    style = MaterialTheme.typography.titleMedium
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(favorites.take(8), key = { it.code ?: it.hashCode() }) { p ->
                        Column(
                            modifier = Modifier
                                .width(110.dp)
                                .clickable { onOpen(p) }
                        ) {
                            ProductImage(
                                url = p.imageSmallUrl ?: p.imageUrl,
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Text(
                                p.productName.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
