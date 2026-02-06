package com.shub39.grit.core.utils

import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "85%", fontScale = 0.85f)
@Preview(name = "100%", fontScale = 1.0f)
@Preview(name = "115%", fontScale = 1.15f)
@Preview(name = "130%", fontScale = 1.3f)
@Preview(name = "150%", fontScale = 1.5f)
@Preview(name = "180%", fontScale = 1.8f)
@Preview(name = "200%", fontScale = 2f)
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
annotation class AllPreviews