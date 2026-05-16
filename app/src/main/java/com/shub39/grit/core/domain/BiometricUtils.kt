package com.shub39.grit.core.domain

interface BiometricUtils {
    fun getAuthenticators(): Int
    fun authenticationAvailable(): Boolean
}