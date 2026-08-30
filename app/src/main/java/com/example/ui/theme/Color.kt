package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==================== CLEAN MINIMALISM PALETTE ====================

// Base Canvas & Surfaces (Clean Whites & Slate Tints)
val SlateBackground = Color(0xFFF8FAFC)        // Slate 50 - Clean light canvas
val SlateSurface = Color(0xFFFFFFFF)           // Pure Crisp White - Elevated cards
val SlateSurfaceVariant = Color(0xFFF1F5F9)    // Slate 100 - Secondary containers & fields
val SlateBorder = Color(0xFFE2E8F0)            // Slate 200 - Minimal crisp dividers & borders
val SlateBorderSubtle = Color(0xFFF1F5F9)      // Slate 100 - Subtle outlines

// Typography (High-Clarity Slate Scale)
val SlateTextPrimary = Color(0xFF0F172A)       // Slate 900 - Deep crisp headings & main text
val SlateTextSecondary = Color(0xFF475569)     // Slate 600 - Readable subheadings & body
val SlateTextMuted = Color(0xFF94A3B8)         // Slate 400 - Timestamps & captions

// Single Hero Accent Color (Refined Professional Indigo-Blue)
val AccentPrimary = Color(0xFF2563EB)          // Royal Blue - Primary actions, active tabs & highlights
val AccentPrimaryLight = Color(0xFFEFF6FF)     // Soft Blue Container
val AccentOnPrimaryLight = Color(0xFF1E40AF)   // High-contrast Blue text for pills & tags

// Secondary Muted Functional Tone
val SlateSecondary = Color(0xFF334155)         // Slate 700 - Professional dark slate actions
val SlateSecondaryContainer = Color(0xFFE2E8F0)// Slate 200 - Inactive badges & button backgrounds
val SlateOnSecondaryContainer = Color(0xFF0F172A) // Slate 900

// Status & Semantic Colors (Subtle & High-Contrast)
val StatusSuccess = Color(0xFF16A34A)          // Forest Green - Approvals & Incoming cash
val StatusSuccessBg = Color(0xFFDCFCE7)        // Light Green tint
val StatusWarning = Color(0xFFD97706)          // Amber - Pending states & alerts
val StatusWarningBg = Color(0xFFFEF3C7)        // Light Amber tint
val StatusError = Color(0xFFDC2626)            // Crimson Red - Expenses, rejections & debits
val StatusErrorBg = Color(0xFFFEE2E2)          // Light Red tint
val StatusInfo = Color(0xFF0284C7)             // Sky Blue - Informational notices
val StatusInfoBg = Color(0xFFE0F2FE)           // Light Sky tint

// Minimalist Aliases for backwards compatibility across screens
val MinimalistBg = SlateBackground
val MinimalistSurface = SlateSurface
val MinimalistSurfaceVariant = SlateSurfaceVariant
val MinimalistBorder = SlateBorder
val MinimalistBorderFocused = SlateBorder
val MinimalistTextPrimary = SlateTextPrimary
val MinimalistTextSecondary = SlateTextSecondary
val MinimalistTextMuted = SlateTextMuted
val MinimalistPrimary = AccentPrimary
val MinimalistPrimaryContainer = AccentPrimaryLight
val MinimalistOnPrimaryContainer = AccentOnPrimaryLight
val MinimalistSecondary = SlateSecondary
val MinimalistSecondaryContainer = SlateSecondaryContainer
val MinimalistOnSecondaryContainer = SlateOnSecondaryContainer
val MinimalistSuccess = StatusSuccess
val MinimalistSuccessContainer = StatusSuccessBg
val MinimalistSuccessText = StatusSuccess
val MinimalistWarning = StatusWarning
val MinimalistWarningContainer = StatusWarningBg
val MinimalistWarningText = StatusWarning
val MinimalistError = StatusError
val MinimalistErrorContainer = StatusErrorBg
val MinimalistErrorText = StatusError
val MinimalistInfo = StatusInfo
val MinimalistInfoContainer = StatusInfoBg
val MinimalistInfoText = StatusInfo
