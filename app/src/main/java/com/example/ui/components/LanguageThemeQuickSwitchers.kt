package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.util.AppLanguage
import com.example.util.AppSettingsManager
import com.example.util.AppThemeMode
import com.example.util.tr

/**
 * Compact Quick Switchers for Language & Theme displayed in TopBars or Headers.
 */
@Composable
fun LanguageThemeQuickSwitchers(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val language by AppSettingsManager.language.collectAsState()
    val themeMode by AppSettingsManager.themeMode.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        AppSettingsDialog(onDismiss = { showSettingsDialog = false })
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Language Switcher Chip / Button
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (language == AppLanguage.TAMIL) Color(0xFF3B82F6).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.15f),
            border = BorderStroke(1.dp, if (language == AppLanguage.TAMIL) Color(0xFF3B82F6) else Color(0xFF10B981)),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { AppSettingsManager.toggleLanguage() }
                .testTag("btn_toggle_language")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = "Language",
                    tint = if (language == AppLanguage.TAMIL) Color(0xFF2563EB) else Color(0xFF059669),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (language == AppLanguage.TAMIL) "தமிழ்" else "ENG",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (language == AppLanguage.TAMIL) Color(0xFF2563EB) else Color(0xFF059669)
                )
            }
        }

        // Theme Switcher Chip / Button
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (themeMode == AppThemeMode.DARK) Color(0xFF1E293B) else Color(0xFFFEF3C7),
            border = BorderStroke(1.dp, if (themeMode == AppThemeMode.DARK) Color(0xFF475569) else Color(0xFFF59E0B)),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { AppSettingsManager.toggleTheme() }
                .testTag("btn_toggle_theme")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (themeMode == AppThemeMode.DARK) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "Theme",
                    tint = if (themeMode == AppThemeMode.DARK) Color(0xFF93C5FD) else Color(0xFFD97706),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (themeMode == AppThemeMode.DARK) {
                        tr("கருப்பு", "Dark", language)
                    } else {
                        tr("கார்ப்பரேட்", "Corp", language)
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (themeMode == AppThemeMode.DARK) Color(0xFFE2E8F0) else Color(0xFF92400E)
                )
            }
        }
    }
}

/**
 * Full Dialog to adjust Language (Tamil / English) and Theme (Dark / Corporate).
 */
@Composable
fun AppSettingsDialog(onDismiss: () -> Unit) {
    val language by AppSettingsManager.language.collectAsState()
    val themeMode by AppSettingsManager.themeMode.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("dialog_app_settings"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dialog Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = tr("அமைப்புகள் (Settings)", "App Settings", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider()

                // 1. Language Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = tr("மொழி தேர்வு (Choose Language)", "Select Language", language),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Tamil Option
                        val isTamil = language == AppLanguage.TAMIL
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { AppSettingsManager.setLanguage(AppLanguage.TAMIL) }
                                .testTag("btn_select_lang_ta"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isTamil) Color(0xFF3B82F6).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(if (isTamil) 2.dp else 1.dp, if (isTamil) Color(0xFF2563EB) else Color.Transparent)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("தமிழ்", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                Text("Tamil", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (isTamil) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // English Option
                        val isEn = language == AppLanguage.ENGLISH
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { AppSettingsManager.setLanguage(AppLanguage.ENGLISH) }
                                .testTag("btn_select_lang_en"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isEn) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(if (isEn) 2.dp else 1.dp, if (isEn) Color(0xFF059669) else Color.Transparent)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("English", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                Text("ஆங்கிலம்", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (isEn) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // 2. Theme Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = tr("ஸ்கிரீன் தீம் (Display Theme)", "Display Theme", language),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Dark Theme (இப்பொழுது இருக்கும் கருப்பு கலர்)
                        val isDark = themeMode == AppThemeMode.DARK
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { AppSettingsManager.setThemeMode(AppThemeMode.DARK) }
                                .testTag("btn_select_theme_dark"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFF0F172A).copy(alpha = 0.5f)
                            ),
                            border = BorderStroke(if (isDark) 2.dp else 1.dp, if (isDark) Color(0xFF8B5CF6) else Color(0xFF334155))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.DarkMode, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(22.dp))
                                Text("கருப்பு தீம்", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("Dark Theme", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                if (isDark) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Corporate Theme (கார்ப்பரேட் கலர்)
                        val isCorp = themeMode == AppThemeMode.CORPORATE
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { AppSettingsManager.setThemeMode(AppThemeMode.CORPORATE) }
                                .testTag("btn_select_theme_corporate"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCorp) Color(0xFFDBEAFE) else Color(0xFFF1F5F9)
                            ),
                            border = BorderStroke(if (isCorp) 2.dp else 1.dp, if (isCorp) Color(0xFF1E40AF) else Color(0xFFCBD5E1))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF1E40AF), modifier = Modifier.size(22.dp))
                                Text("கார்ப்பரேட்", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E3A8A))
                                Text("Corporate Theme", fontSize = 10.sp, color = Color(0xFF64748B))
                                if (isCorp) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF1E40AF), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Done Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(tr("சரி (Save)", "Done", language), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
