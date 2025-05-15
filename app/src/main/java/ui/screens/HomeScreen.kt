package com.kavyakanaja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kavyakanaja.app.ui.navigation.Screen
import com.kavyakanaja.app.viewmodel.PoemViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.kavyakanaja.app.AppLanguage
import com.kavyakanaja.app.LanguageManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: PoemViewModel = hiltViewModel(),
    languageManager: LanguageManager
) {
    val uiState  by viewModel.uiState.collectAsState()
    val language by languageManager.language.collectAsState()
    val poem = uiState.poemOfDay

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (language == AppLanguage.KANNADA) "ಕಾವ್ಯ-ಕಣಜ"
                        else "Kavya-Kanaja",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    // Language toggle button
                    TextButton(onClick = { languageManager.toggle() }) {
                        Text(
                            if (language == AppLanguage.KANNADA) "EN" else "ಕನ್ನಡ",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                        Icon(Icons.Default.Search, null,
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, null) },
                    label = {
                        Text(if (language == AppLanguage.KANNADA) "ಮನೆ" else "Home")
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Search.route) },
                    icon = { Icon(Icons.Default.Search, null) },
                    label = {
                        Text(if (language == AppLanguage.KANNADA) "ಹುಡುಕು" else "Search")
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Bookmarks.route) },
                    icon = { Icon(Icons.Default.Bookmark, null) },
                    label = {
                        Text(if (language == AppLanguage.KANNADA) "ಉಳಿಸಿದ" else "Saved")
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Profile.route) },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = {
                        Text(if (language == AppLanguage.KANNADA) "ಪ್ರೊಫೈಲ್" else "Profile")
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Date header
            val today = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH).format(Date())
            Text(today, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)

            Spacer(Modifier.height(8.dp))
            Text("ಇಂದಿನ ಕವಿತೆ", style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary)
            Text("Poem of the Day", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary)

            Spacer(Modifier.height(20.dp))

            poem?.let { p ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp),
                    onClick = {
                        viewModel.selectPoem(p)
                        navController.navigate(Screen.PoemDetail.createRoute(p.id))
                    }
                ) {
                    Column(Modifier.padding(20.dp)) {
                        // Poet tag
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                "  ${p.poet} · ${p.era}  ",
                                modifier = Modifier.padding(vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(p.titleKannada, style = MaterialTheme.typography.headlineMedium)
                        Text(p.title, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
                        Spacer(Modifier.height(16.dp))
                        Text(
                            p.verse.take(120) + "...",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Tap to read, listen & explore →",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Poet's Corner button
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Poet.createRoute(p.poetId)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Person, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Explore ${p.poet}'s Corner")
                }
            }
        }
    }
}
