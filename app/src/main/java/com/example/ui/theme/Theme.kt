package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.util.AppSettingsManager
import com.example.util.AppThemeMode

// கார்ப்பரேட் கலர் (Corporate Clean Light Palette)
private val CorporateColorScheme = lightColorScheme(
    primary = CorporatePrimary,
    onPrimary = CorporateOnPrimary,
    primaryContainer = CorporatePrimaryContainer,
    onPrimaryContainer = CorporateOnPrimaryContainer,
    secondary = CorporateSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF115E59),
    tertiary = Color(0xFF2563EB),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDBEAFE),
    onTertiaryContainer = Color(0xFF1E40AF),
    background = CorporateBackground,
    onBackground = Color(0xFF0F172A),
    surface = CorporateSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = CorporateOutline
)

// இப்பொழுது இருக்கும் கருப்பு கலர் (Deep Dark Neon Palette)
private val DarkColorScheme = darkColorScheme(
    primary = BrandBluePrimaryDark,
    onPrimary = Color(0xFF00344F),
    primaryContainer = BrandBlueContainerDark,
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFF1F5F9),
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = Color(0xFF78350F),
    background = BrandBackgroundDark,
    onBackground = Color(0xFFF8FAFC),
    surface = BrandSurfaceDark,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = BrandOutlineDark
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppSettingsManager.themeMode.collectAsState().value,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.CORPORATE -> CorporateColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
