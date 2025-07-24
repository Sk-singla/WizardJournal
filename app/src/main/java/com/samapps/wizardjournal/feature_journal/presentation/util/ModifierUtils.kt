package com.samapps.wizardfeature_presentation.util

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundInfo
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundType

fun getColors(backgroundInfo: BackgroundInfo): List<Color> {
    when (backgroundInfo.type) {
        BackgroundType.SOLID_COLOR -> {
            if (backgroundInfo.primaryColor != null) {
                return listOf(Color(backgroundInfo.primaryColor.colorCode))
            }
        }

        BackgroundType.LINEAR_GRADIENT -> {
            if (backgroundInfo.primaryColor != null && backgroundInfo.secondaryColor != null) {
                return (listOf(
                    Color(backgroundInfo.primaryColor.colorCode),
                    Color(backgroundInfo.secondaryColor.colorCode)
                ))
            }
        }

        BackgroundType.RADIAL_GRADIENT -> {
            if (backgroundInfo.primaryColor != null && backgroundInfo.secondaryColor != null) {
                return (listOf(
                    Color(backgroundInfo.primaryColor.colorCode),
                    Color(backgroundInfo.secondaryColor.colorCode)
                ))
            }
        }
    }

    return listOf()
}
//{"type":"LINEAR_GRADIENT","primaryColor":"ROYAL_BLUE","secondaryColor":"EMERALD_GREEN","gradientAngle":135.0}
fun Modifier.customBackground(backgroundInfo: BackgroundInfo): Modifier {
//    return this

    try {
        val colors = getColors(backgroundInfo)
        if (colors.size == 1) {
            return this.background(colors.first())
        } else if (colors.size == 2) {
            if (backgroundInfo.type == BackgroundType.LINEAR_GRADIENT) {
                return this.background(
                    brush = Brush.linearGradient(
                        colors = colors
                    )
                )
            } else if (backgroundInfo.type == BackgroundType.RADIAL_GRADIENT) {
                return this.background(
                    brush = Brush.radialGradient(
                        colors = colors
                    )
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return this
}