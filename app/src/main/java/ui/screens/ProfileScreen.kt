package com.kavyakanaja.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kavyakanaja.app.ui.navigation.Screen
import com.kavyakanaja.app.viewmodel.AuthViewModel
import com.kavyakanaja.app.viewmodel.PoemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    poemViewModel: PoemViewModel = hiltViewModel()
) {
    val auth  by authViewModel.state.collectAsState()
    val poems by poemViewModel.uiState.collectAsState()
    var showLogout by remember { mutableStateOf(false) }

    // If not logged in send to login
    if (!auth.isLoggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Profile.route) { inclusive = true }
            }
        }
        return
    }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            icon = {
                Icon(Icons.Default.Logout, null,
                    tint = MaterialTheme.colorScheme.error)
            },
            title = { Text("Sign Out?") },
            text = { Text("You will be signed out of Kavya-Kanaja.") },
            confirmButton = {
                TextButton(onClick = {
                    authViewModel.logout()
                    showLogout = false
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Text("Yes, Sign Out",
                        color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogout = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ನನ್ನ ಪ್ರೊಫೈಲ್",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary)
                        Text("My Profile",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null,
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showLogout = true }) {
                        Icon(Icons.Default.Logout, "Logout",
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Search.route) },
                    icon = { Icon(Icons.Default.Search, null) },
                    label = { Text("Search") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Bookmarks.route) },
                    icon = { Icon(Icons.Default.Bookmark, null) },
                    label = { Text("Saved") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Avatar section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.07f))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                auth.userName.firstOrNull()?.uppercase() ?: "K",
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(auth.userName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground)
                        Text(auth.userEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary)
                        Surface(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                "  ✓  Active Account  ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Stats
            item {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Bookmark,
                        value = "${poems.bookmarks.size}",
                        label = "Poems Saved"
                    )
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.MenuBook,
                        value = "${poemViewModel.getAllPoems().size}",
                        label = "Total Poems"
                    )
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.People,
                        value = "7",
                        label = "Poets"
                    )
                }
            }

            // Account info
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Account Details",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        ProfileInfoRow(Icons.Default.Person, "Name", auth.userName)
                        HorizontalDivider()
                        ProfileInfoRow(Icons.Default.Email, "Email", auth.userEmail)
                        HorizontalDivider()
                        ProfileInfoRow(Icons.Default.Shield, "Account Status", "Active")
                        HorizontalDivider()
                        ProfileInfoRow(Icons.Default.Language, "App", "Kavya-Kanaja v1.0")
                    }
                }
            }

            // Saved poems preview
            if (poems.bookmarks.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Recently Saved",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            poems.bookmarks.take(3).forEach { bookmark ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.Bookmark, null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(bookmark.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface)
                                        Text(bookmark.poet,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                                if (bookmark != poems.bookmarks.take(3).last()) {
                                    HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Sign out
            item {
                Spacer(Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { showLogout = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Logout, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out", style = MaterialTheme.typography.titleSmall)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProfileStatCard(
    modifier: Modifier,
    icon: ImageVector,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp))
            Text(value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary)
            Text(label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(icon, null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp))
        Column {
            Text(label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary)
            Text(value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface)
        }
    }
}