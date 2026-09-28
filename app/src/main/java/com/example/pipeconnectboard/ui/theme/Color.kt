package com.example.pipeconnectboard.ui.theme

import androidx.compose.ui.graphics.Color

// Industrial Graphite & Steel Theme Palette
val BackgroundDark = Color(0xFF0D1219)
val SurfaceDark = Color(0xFF151C26)
val SurfaceLightDark = Color(0xFF1E2736)
val CardBackgroundDark = Color(0xFF18202D)
val BorderDark = Color(0xFF2C384A)

// Text Colors
val TextPrimaryDark = Color(0xFFE2E8F0)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextTertiaryDark = Color(0xFF64748B)

// Accent Colors
val AccentTeal = Color(0xFF14B8A6)
val AccentTealBright = Color(0xFF2DD4BF)
val AccentTealFlow = Color(0xFF06B6D4)
val AccentAmberSource = Color(0xFFF59E0B)
val AccentEmeraldDestination = Color(0xFF10B981)
val AccentRedBlocked = Color(0xFF475569)

// Metallic Pipe Finishes
val SteelPipeOuter = Color(0xFF78889B)
val SteelPipeInner = Color(0xFF475569)
val SteelPipeHighlight = Color(0xFFCBD5E1)
val PipeChannelDark = Color(0xFF0F172A)

// Flow Pulse Colors
val FlowGlowTeal = Color(0x6606B6D4)
val FlowBrightCore = Color(0xFFE0F2FE)

// Light Theme Palette (Clean Technical Blueprint Aesthetic)
val BackgroundLight = Color(0xFFF1F5F9)
val SurfaceLight = Color(0xFFFFFFFF)
val CardBackgroundLight = Color(0xFFF8FAFC)
val BorderLight = Color(0xFFCBD5E1)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextTertiaryLight = Color(0xFF64748B)

// Board Theme Data
data class BoardThemeColors(
    val boardBackground: Color,
    val gridBorder: Color,
    val emptyCellBackground: Color,
    val emptyCellCross: Color,
    val blockedPlateBackground: Color,
    val blockedHatchColor: Color,
    val pipeBody: Color,
    val pipeCore: Color,
    val pipeHighlight: Color,
    val flowGlow: Color,
    val flowCore: Color,
    val sourceColor: Color,
    val destinationColor: Color
)

val ClassicBoardTheme = BoardThemeColors(
    boardBackground = Color(0xFF111722),
    gridBorder = Color(0xFF243042),
    emptyCellBackground = Color(0xFF17202E),
    emptyCellCross = Color(0xFF243144),
    blockedPlateBackground = Color(0xFF1F2837),
    blockedHatchColor = Color(0xFF2D3B4F),
    pipeBody = Color(0xFF7A8B9E),
    pipeCore = Color(0xFF1E2838),
    pipeHighlight = Color(0xFFD6E0EA),
    flowGlow = Color(0xFF0891B2),
    flowCore = Color(0xFF67E8F9),
    sourceColor = Color(0xFFF59E0B),
    destinationColor = Color(0xFF10B981)
)

val MidnightBoardTheme = BoardThemeColors(
    boardBackground = Color(0xFF090D14),
    gridBorder = Color(0xFF1B2433),
    emptyCellBackground = Color(0xFF101724),
    emptyCellCross = Color(0xFF1E2B3E),
    blockedPlateBackground = Color(0xFF172030),
    blockedHatchColor = Color(0xFF243249),
    pipeBody = Color(0xFF8692A6),
    pipeCore = Color(0xFF131B29),
    pipeHighlight = Color(0xFFE2E8F0),
    flowGlow = Color(0xFF0284C7),
    flowCore = Color(0xFF38BDF8),
    sourceColor = Color(0xFFFB923C),
    destinationColor = Color(0xFF34D399)
)