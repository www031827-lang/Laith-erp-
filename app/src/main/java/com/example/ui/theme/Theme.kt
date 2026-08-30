package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// ==================== CLEAN MINIMALISM SCHEMES ====================

// Muted, professional light color scheme (Slate Grays, Clean Whites & Accent Blue)
private val CleanMinimalistLightColorScheme = lightColorScheme(
    primary = AccentPrimary,                   // Professional Royal Blue (#2563EB)
    onPrimary = Color.White,
    primaryContainer = AccentPrimaryLight,     // Soft Slate-Blue Container (#EFF6FF)
    onPrimaryContainer = AccentOnPrimaryLight, // (#1E40AF)
    
    secondary = SlateSecondary,                // Slate 700 (#334155)
    onSecondary = Color.White,
    secondaryContainer = SlateSecondaryContainer, // Slate 200 (#E2E8F0)
    onSecondaryContainer = SlateOnSecondaryContainer,
    
    tertiary = StatusInfo,                     // Sky Blue (#0284C7)
    onTertiary = Color.White,
    tertiaryContainer = StatusInfoBg,
    onTertiaryContainer = StatusInfo,
    
    background = SlateBackground,              // Slate 50 - Crisp Clean Light Canvas (#F8FAFC)
    onBackground = SlateTextPrimary,           // Slate 900 (#0F172A)
    
    surface = SlateSurface,                    // Pure White (#FFFFFF)
    onSurface = SlateTextPrimary,              // Slate 900
    surfaceVariant = SlateSurfaceVariant,      // Slate 100 (#F1F5F9)
    onSurfaceVariant = SlateTextSecondary,     // Slate 600 (#475569)
    surfaceTint = AccentPrimary,
    
    outline = SlateBorder,                     // Slate 200 (#E2E8F0)
    outlineVariant = SlateBorderSubtle,        // Slate 100 (#F1F5F9)
    
    error = StatusError,                       // Crimson Red (#DC2626)
    onError = Color.White,
    errorContainer = StatusErrorBg,            // (#FEE2E2)
    onErrorContainer = StatusError
)

// Minimalist Dark Scheme (Deep Slate Charcoal & Muted Surfaces)
private val CleanMinimalistDarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),               // Light Accent Blue
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    
    secondary = Color(0xFF94A3B8),             // Slate 400
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFF1F5F9),
    
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF0F172A),
    tertiaryContainer = Color(0xFF075985),
    onTertiaryContainer = Color(0xFFE0F2FE),
    
    background = Color(0xFF0F172A),            // Deep Slate 900
    onBackground = Color(0xFFF8FAFC),
    
    surface = Color(0xFF1E293B),               // Slate 800
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),        // Slate 700
    onSurfaceVariant = Color(0xFFCBD5E1),
    
    outline = Color(0xFF475569),               // Slate 600
    outlineVariant = Color(0xFF334155),
    
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

// Clean Minimalist Shapes (Subtle, refined radii)
val CleanMinimalistShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun CleanMinimalismTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) CleanMinimalistDarkColorScheme else CleanMinimalistLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = CleanMinimalistShapes,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    CleanMinimalismTheme(darkTheme = darkTheme, content = content)
}
