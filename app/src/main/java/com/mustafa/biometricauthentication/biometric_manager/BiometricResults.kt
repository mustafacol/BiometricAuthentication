package com.mustafa.biometricauthentication.biometric_manager

sealed interface BiometricResults {
    data object HardwareUnavailable : BiometricResults
    data object FeatureUnavailable : BiometricResults
    data class AuthenticationError(val error: String) : BiometricResults
    data object AuthenticationFailed : BiometricResults
    data object AuthenticationSuccess : BiometricResults
    data object AuthenticationNotSet : BiometricResults
}