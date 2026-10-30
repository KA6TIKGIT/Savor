package com.example.savor.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.savor.R
import com.example.savor.data.local.SortOrder
import com.example.savor.data.remote.Product

@Composable
fun ExploreScreen(
    vm: SavorViewModel,
    onOpen: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val query by vm.query.collectAsState()
    val products by vm.products.collectAsState()
    val loading by vm.loading.collectAsState()
    val hasError by vm.hasError.collectAsState()
    val hasSearched by vm.hasSearched.collectAsState()
    var showSettings by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val performSearch = {
        keyboardController?.hide()
        vm.search()
    }

    Column(modifier.padding(horizontal = dimensionResource(R.dimen.screen_padding))) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = vm::onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.search_hint)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { performSearch() }),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { vm.onQueryChange("") }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.clear_search)
                            )
                        }
                    } else {
                        IconButton(onClick = { performSearch() }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                    }
                }
            )
            IconButton(onClick = { showSettings = true }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            }
        }

        when {
            loading -> CenteredBox { CircularProgressIndicator() }
            hasError -> CenteredBox {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(R.string.error_network),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(onClick = { performSearch() }) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
            hasSearched && products.isEmpty() -> CenteredBox {
                Text(
                    stringResource(R.string.no_results),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            !hasSearched && products.isEmpty() -> CenteredBox {
                Text(
                    stringResource(R.string.search_prompt),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(products, key = { it.code ?: it.hashCode() }) { product ->
                    ProductRow(product) { onOpen(product) }
                }
            }
        }
    }

    if (showSettings) SettingsDialog(vm) { showSettings = false }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@Composable
fun SettingsDialog(vm: SavorViewModel, onDismiss: () -> Unit) {
    val dark by vm.darkTheme.collectAsState()
    val sort by vm.sortOrder.collectAsState()
    val options = listOf(
        SortOrder.DEFAULT to R.string.sort_default,
        SortOrder.NAME to R.string.sort_name,
        SortOrder.NUTRISCORE to R.string.sort_nutriscore
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.ok)) } },
        title = { Text(stringResource(R.string.settings)) },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.dark_theme), Modifier.weight(1f))
                    Switch(checked = dark, onCheckedChange = vm::setDarkTheme)
                }
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.sort_by), style = MaterialTheme.typography.titleSmall)
                options.forEach { (order, label) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { vm.setSortOrder(order) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sort == order,
                            onClick = { vm.setSortOrder(order) }
                        )
                        Text(stringResource(label))
                    }
                }
            }
        }
    )
}
