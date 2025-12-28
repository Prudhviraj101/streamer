package com.example.stream.ui.screens.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stream.ui.theme.GlassBackground
import com.example.stream.ui.theme.GlassPrimary
import com.example.stream.ui.theme.TextPrimary
import com.example.stream.ui.theme.TextSecondary
import com.example.stream.util.rememberWindowSizeClass
import com.example.stream.util.getTitleFontSize
import com.example.stream.util.getVerticalSpacing
import java.io.File
import kotlin.math.pow

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var cacheSize by remember { mutableStateOf(0L) }
    var totalAppData by remember { mutableStateOf(0L) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showEraseDataDialog by remember { mutableStateOf(false) }
    
    val windowSize = rememberWindowSizeClass()
    val titleSize = windowSize.getTitleFontSize()
    val verticalSpacing = windowSize.getVerticalSpacing()
    
    // Load DNS preference
    var customDnsEnabled by remember { 
        mutableStateOf(com.example.stream.util.DnsPreferences.isCustomDnsEnabled(context)) 
    }
    
    // Calculate sizes
    LaunchedEffect(Unit) {
        cacheSize = calculateCacheSize(context)
        totalAppData = calculateTotalAppData(context)
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        // Header
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = titleSize,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        
        
        // Storage Section
        item {
            SettingsSection(title = "Storage") {
                // Cache info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Cache",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Images and temporary data",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    
                    Text(
                        text = formatFileSize(cacheSize),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                
                // Clear Cache
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Clear Cache",
                    subtitle = null,
                    onClick = { showClearCacheDialog = true },
                    iconTint = Color(0xFF6366F1)
                )
                
                // Total App Data info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total App Data",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "All app data including downloads",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    
                    Text(
                        text = formatFileSize(totalAppData),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                
                // Erase All Data
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEraseDataDialog = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Text(
                        text = "Erase All Data",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        // General Section
        item {
            SettingsSection(title = "General") {
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = null,
                    onClick = { /* TODO: Navigate to notifications */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Privacy",
                    subtitle = null,
                    onClick = { /* TODO: Navigate to privacy */ }
                )
            }
        }
        
        // DNS & Network Section
        item {
            SettingsSection(title = "DNS & Network") {
                // Custom DNS Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Custom DNS",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (customDnsEnabled) "Bypassing AdGuard DNS" else "Using system DNS",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (customDnsEnabled) Color(0xFF4CAF50) else TextSecondary
                        )
                    }
                    
                    Switch(
                        checked = customDnsEnabled,
                        onCheckedChange = { enabled ->
                            customDnsEnabled = enabled
                            com.example.stream.util.DnsPreferences.setCustomDnsEnabled(context, enabled)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAF50),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray
                        )
                    )
                }
                
                // Show DNS details only when enabled
                androidx.compose.animation.AnimatedVisibility(visible = customDnsEnabled) {
                    Column {
                        Divider(
                            color = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        // Primary DNS
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Primary DNS",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "8.8.8.8 (Google DNS)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                        
                        Divider(
                            color = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        // Secondary DNS
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Fallback DNS",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "1.1.1.1 (Cloudflare DNS)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // About Us Section
        item {
            SettingsSection(title = "About") {
                // Version - non-clickable info row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Version",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                
                // Build - non-clickable info row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Build",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "1",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                
                SettingsItem(
                    icon = Icons.Default.Code,
                    title = "GitHub",
                    subtitle = null,
                    onClick = { /* TODO: Open GitHub */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.Chat,
                    title = "Discord",
                    subtitle = null,
                    onClick = { /* TODO: Open Discord */ }
                )
            }
        }
        
        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // Clear Cache Confirmation Dialog
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("Clear Cache?") },
            text = {
                Text("This will clear ${formatFileSize(cacheSize)} of cached data. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clearCache(context)
                        cacheSize = calculateCacheSize(context)
                        totalAppData = calculateTotalAppData(context)
                        showClearCacheDialog = false
                    }
                ) {
                    Text("Clear", color = GlassPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Erase All Data Confirmation Dialog
    if (showEraseDataDialog) {
        AlertDialog(
            onDismissRequest = { showEraseDataDialog = false },
            title = { Text("Erase All Data?", color = Color(0xFFEF4444)) },
            text = {
                Text("This will erase ALL app data including downloads, watch history, and settings. This action cannot be undone!")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clearAllData(context)
                        cacheSize = 0L
                        totalAppData = 0L
                        showEraseDataDialog = false
                    }
                ) {
                    Text("Erase", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEraseDataDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1E1E1E)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    iconTint: Color = GlassPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GlassPrimary,
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GlassPrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray
            )
        )
    }
}

@Composable
private fun SettingsDropdown(
    icon: ImageVector,
    title: String,
    subtitle: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GlassPrimary,
                    modifier = Modifier.size(24.dp)
                )
                
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1E1E1E))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = TextPrimary) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun calculateCacheSize(context: Context): Long {
    var size = 0L
    context.cacheDir?.let { cacheDir -> size += getFolderSize(cacheDir) }
    context.externalCacheDir?.let { externalCacheDir -> size += getFolderSize(externalCacheDir) }
    return size
}

private fun calculateTotalAppData(context: Context): Long {
    var size = 0L
    
    // Cache
    size += calculateCacheSize(context)
    
    // Internal data directory
    context.filesDir?.let { filesDir -> size += getFolderSize(filesDir) }
    
    // External files directory
    context.getExternalFilesDir(null)?.let { externalFilesDir -> size += getFolderSize(externalFilesDir) }
    
    return size
}

private fun getFolderSize(folder: File): Long {
    var size = 0L
    if (folder.exists()) {
        folder.listFiles()?.forEach { file ->
            size += if (file.isDirectory) getFolderSize(file) else file.length()
        }
    }
    return size
}

private fun clearCache(context: Context) {
    context.cacheDir?.let { deleteRecursive(it) }
    context.externalCacheDir?.let { deleteRecursive(it) }
}

private fun clearAllData(context: Context) {
    // Clear cache
    clearCache(context)
    
    // Clear internal files
    context.filesDir?.let { filesDir ->
        filesDir.listFiles()?.forEach { file ->
            deleteRecursive(file)
        }
    }
    
    // Clear external files
    context.getExternalFilesDir(null)?.let { externalFilesDir ->
        externalFilesDir.listFiles()?.forEach { file ->
            deleteRecursive(file)
        }
    }
}

private fun deleteRecursive(fileOrDirectory: File) {
    if (fileOrDirectory.isDirectory) {
        fileOrDirectory.listFiles()?.forEach { child -> deleteRecursive(child) }
    }
    fileOrDirectory.delete()
}

private fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.2f %s", size / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
}
