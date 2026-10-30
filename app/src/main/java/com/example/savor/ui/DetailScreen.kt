package com.example.savor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.savor.R

@Composable
fun DetailScreen(
    vm: SavorViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product by vm.selected.collectAsState()
    val isFavorite by vm.selectedIsFavorite.collectAsState()
    val reviews by vm.selectedReviews.collectAsState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val p = product
    if (p == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    stringResource(R.string.no_results),
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(onClick = onBack) {
                    Text(stringResource(R.string.back))
                }
            }
        }
        return
    }

    var rating by remember { mutableIntStateOf(5) }
    var reviewText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
                IconButton(onClick = vm::toggleFavorite) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(
                            if (isFavorite) R.string.remove_from_favorites else R.string.add_to_favorites
                        ),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        item {
            ProductImage(
                url = p.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.card_corner))),
                contentScale = ContentScale.Fit
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(p.productName.orEmpty(), style = MaterialTheme.typography.headlineSmall)
                Text(
                    p.brands ?: stringResource(R.string.unknown_brand),
                    style = MaterialTheme.typography.titleMedium
                )
                GradeBadge(p.nutriscoreGrade)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { IntentHelper.openInBrowser(context, p) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.open_in_browser)) }
                OutlinedButton(
                    onClick = { IntentHelper.shareByEmail(context, p) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.share_by_email)) }
            }
        }
        if (!p.categories.isNullOrBlank()) {
            item {
                Column {
                    Text(stringResource(R.string.categories), style = MaterialTheme.typography.titleSmall)
                    Text(p.categories, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        if (!p.ingredientsText.isNullOrBlank()) {
            item {
                Column {
                    Text(stringResource(R.string.ingredients), style = MaterialTheme.typography.titleSmall)
                    Text(p.ingredientsText, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // ---- Personal reviews ----
        item {
            Text(stringResource(R.string.your_review), style = MaterialTheme.typography.titleMedium)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.rating))
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = pluralStringResource(R.plurals.star_rating_content_description, star, star),
                                tint = if (star <= rating) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.review_hint)) }
                )
                Button(
                    onClick = {
                        keyboardController?.hide()
                        vm.addReview(rating, reviewText)
                        reviewText = ""
                    },
                    enabled = reviewText.isNotBlank()
                ) { Text(stringResource(R.string.save_review)) }
            }
        }
        if (reviews.isEmpty()) {
            item { Text(stringResource(R.string.no_reviews)) }
        }
        items(reviews, key = { it.id }) { review ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("★".repeat(review.rating), color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(4.dp))
                        Text(review.text)
                    }
                    IconButton(onClick = { vm.deleteReview(review) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_review)
                        )
                    }
                }
            }
        }
    }
}
