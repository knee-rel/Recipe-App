// SettingsScreen.kt
package com.example.recipeapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette

@Composable
fun SettingsScreen() {
    var isDarkTheme by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showAboutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Appearance Section
        item {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Create,
                title = "Dark Theme",
                subtitle = "Switch between light and dark theme",
                trailing = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isDarkTheme = it }
                    )
                }
            )
        }

        // Notifications Section
        item {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Push Notifications",
                subtitle = "Receive notifications about new recipes",
                trailing = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            )
        }

        // Filters Section
        item {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.List,
                title = "Dietary Preferences",
                subtitle = "Set your dietary restrictions and preferences",
                onClick = { /* Navigate to dietary preferences */ }
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Create,
                title = "Cuisine Preferences",
                subtitle = "Choose your preferred cuisines",
                onClick = { /* Navigate to cuisine preferences */ }
            )
        }

        // Data Section
        item {
            Text(
                text = "Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Create,
                title = "Offline Mode",
                subtitle = "Download recipes for offline viewing",
                onClick = { /* Navigate to offline settings */ }
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Delete,
                title = "Clear Cache",
                subtitle = "Free up storage space",
                onClick = { /* Clear cache */ }
            )
        }

        // About Section
        item {
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Info,
                title = "About Recipe App",
                subtitle = "Version 1.0.0",
                onClick = { showAboutDialog = true }
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Star,
                title = "Rate App",
                subtitle = "Leave a review on the Play Store",
                onClick = { /* Open Play Store */ }
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Share,
                title = "Share App",
                subtitle = "Share with friends and family",
                onClick = { /* Share app */ }
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Email,
                title = "Contact Us",
                subtitle = "Send feedback or report issues",
                onClick = { /* Open email */ }
            )
        }
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Recipe App") },
            text = {
                Column {
                    Text("Recipe App v1.0.0")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Discover and save your favorite recipes from around the world.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Powered by TheMealDB API")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick?.invoke() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            trailing?.invoke()
        }
    }
}