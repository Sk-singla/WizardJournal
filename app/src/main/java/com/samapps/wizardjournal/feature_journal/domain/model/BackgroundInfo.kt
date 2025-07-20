package com.samapps.wizardjournal.feature_journal.domain.model

import kotlinx.serialization.Serializable

enum class BackgroundType {
    SOLID_COLOR,
    LINEAR_GRADIENT,
    RADIAL_GRADIENT,
    PATTERN
}

@Serializable
data class BackgroundInfo(
    val type: BackgroundType,
    val primaryColor: String, // Hex code like "#RRGGBB"
    val secondaryColor: String?, // Hex code, for gradients, null otherwise
    val patternKey: String?, // Keyword for predefined patterns (e.g., "stars", "waves"), null otherwise
    val gradientAngle: Float? // For linear gradients, in degrees, null otherwise
)

