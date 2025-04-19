package com.shub39.grit.core.domain

import androidx.annotation.StringRes
import com.shub39.grit.R

enum class AppTheme(
    @StringRes val fullName: Int
) {
    SYSTEM(R.string.system),
    DARK(R.string.dark),
    LIGHT(R.string.light)
}