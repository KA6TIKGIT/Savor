package com.example.savor.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.savor.R
import androidx.compose.material.icons.filled.Home

@Composable
fun SavorApp(vm: SavorViewModel = viewModel()) {
    val dark by vm.darkTheme.collectAsState()

    val colors = if (dark) {
        darkColorScheme(
            primary = colorResource(R.color.savor_secondary),
            secondary = colorResource(R.color.savor_primary)
        )
    } else {
        lightColorScheme(
            primary = colorResource(R.color.savor_primary),
            secondary = colorResource(R.color.savor_secondary),
            background = colorResource(R.color.savor_background),
            surface = colorResource(R.color.savor_background),
            surfaceVariant = colorResource(R.color.savor_card),
            onBackground = colorResource(R.color.savor_text),
            onSurface = colorResource(R.color.savor_text),
            onSurfaceVariant = colorResource(R.color.savor_text)
        )
    }

    MaterialTheme(colorScheme = colors) {
        val nav = rememberNavController()
        val backStack by nav.currentBackStackEntryAsState()
        val route = backStack?.destination?.route

        Scaffold(
            bottomBar = {
                if (route != "detail") {
                    NavigationBar {
                        NavigationBarItem(
                            selected = route == "home",
                            onClick = {
                                nav.navigate("home") {
                                    popUpTo("home")
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            label = { Text(stringResource(R.string.home)) }
                        )
                        NavigationBarItem(
                            selected = route == "explore",
                            onClick = {
                                nav.navigate("explore") {
                                    popUpTo("home")
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(Icons.Default.Search, contentDescription = null) },
                            label = { Text(stringResource(R.string.explore)) }
                        )
                        NavigationBarItem(
                            selected = route == "favorites",
                            onClick = {
                                nav.navigate("favorites") {
                                    popUpTo("home")
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                            label = { Text(stringResource(R.string.favorites)) }
                        )
                    }
                }
            }
        ) { inner ->
            NavHost(
                navController = nav,
                startDestination = "home",
                modifier = Modifier.padding(inner)
            ) {
                composable("home") {
                    HomeScreen(
                        vm,
                        onSearch = { term ->
                            vm.onQueryChange(term)
                            vm.search(term)
                            nav.navigate("explore") {
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        },
                        onOpen = { vm.select(it); nav.navigate("detail") }
                    )
                }
                composable("explore") {
                    ExploreScreen(vm, onOpen = { vm.select(it); nav.navigate("detail") })
                }
                composable("favorites") {
                    FavoritesScreen(vm, onOpen = { vm.select(it); nav.navigate("detail") })
                }
                composable("detail") {
                    DetailScreen(vm, onBack = { nav.popBackStack() })
                }
            }
        }
    }
}