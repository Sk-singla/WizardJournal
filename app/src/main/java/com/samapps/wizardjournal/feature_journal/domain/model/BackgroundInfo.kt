package com.samapps.wizardjournal.feature_journal.domain.model

import kotlinx.serialization.Serializable

enum class BackgroundType {
    SOLID_COLOR,
    LINEAR_GRADIENT,
    RADIAL_GRADIENT,
//    PATTERN
}

enum class CustomColors(val colorCode: Long) {
    ROYAL_BLUE(0xFF4169E1),
    CORAL(0xFFFF7F50),
    SUNSET_ORANGE(0xFFFF5E13),
    TURQUOISE(0xFF40E0D0),
    MAUVE(0xFFE0B0FF),
    GOLDEN_YELLOW(0xFFFFD700),
    TEAL(0xFF008080),
    LAVENDER(0xFFB57EDC),
    BLUSH_PINK(0xFFFFB6C1),
    EMERALD_GREEN(0xFF50C878)
}


@Serializable
data class BackgroundInfo(
    val type: BackgroundType,
    val primaryColor: CustomColors? = null, // Hex value for the primary color, null for auto
    val secondaryColor: CustomColors? = null, // Hex value for the secondary color if gradient colors, null if not applicable
    val patternKey: String? = null, // Keyword for predefined patterns (e.g., "stars", "waves"), null otherwise
    val gradientAngle: Float? = null // For linear gradients, in degrees, null otherwise
)

