package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val code: String, val labelTa: String, val labelEn: String) {
    TAMIL("ta", "தமிழ்", "Tamil"),
    ENGLISH("en", "ஆங்கிலம்", "English")
}

enum class AppThemeMode(val code: String, val labelTa: String, val labelEn: String) {
    DARK("dark", "கருப்பு தீம் (Dark)", "Dark Theme"),
    CORPORATE("corporate", "கார்ப்பரேட் தீம் (Light)", "Corporate Theme")
}

/**
 * Global App Settings Manager for Language (Tamil / English)
 * and Theme (Dark / Corporate Light) switching.
 */
object AppSettingsManager {
    private val _language = MutableStateFlow(AppLanguage.TAMIL)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.TAMIL) AppLanguage.ENGLISH else AppLanguage.TAMIL
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun toggleTheme() {
        _themeMode.value = if (_themeMode.value == AppThemeMode.DARK) AppThemeMode.CORPORATE else AppThemeMode.DARK
    }
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.TAMIL }
val LocalAppThemeMode = compositionLocalOf { AppThemeMode.DARK }

/**
 * Helper to get bilingual string based on active AppLanguage.
 */
fun tr(ta: String, en: String, currentLang: AppLanguage = AppSettingsManager.language.value): String {
    return if (currentLang == AppLanguage.TAMIL) ta else en
}
