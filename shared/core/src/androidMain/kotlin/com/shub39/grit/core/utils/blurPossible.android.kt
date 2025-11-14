package com.shub39.grit.core.utils

import android.os.Build

actual fun blurPossible(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S