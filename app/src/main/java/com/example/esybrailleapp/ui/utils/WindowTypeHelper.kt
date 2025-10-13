package com.example.esybrailleapp.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration


enum class WindowType {
    Compact,
    Medium,
    Expanded
}

@Composable
fun currentWindowType(): WindowType {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    return when {
        screenWidthDp < 600 -> WindowType.Compact
        screenWidthDp < 840 -> WindowType.Medium
        else -> WindowType.Expanded
    }
}
