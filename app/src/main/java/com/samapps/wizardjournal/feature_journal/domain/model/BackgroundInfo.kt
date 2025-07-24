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
    val primaryColor: ULong? = null, // Hex value for the primary color, null for auto
    val secondaryColor: ULong? = null, // Hex value for the secondary color if gradient colors, null if not applicable
    val patternKey: String? = null, // Keyword for predefined patterns (e.g., "stars", "waves"), null otherwise
    val gradientAngle: Float? = null // For linear gradients, in degrees, null otherwise
)

