package com.ghostdev.quickjot

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class FingerprintHelper(private val context: Context, private val executor: Executor) {

    fun isFingerprintAvailable(): Boolean {
        val fingerprintManager = ContextCompat.getSystemService(context, FingerprintManager::class.java)
        return fingerprintManager != null && fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()
    }

    fun showFingerprintPrompt(onSuccess: () -> Unit, onError: () -> Unit) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Jot")
            .setDescription("Use your fingerprint to unlock the jot")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(context as MainActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError()
                    Toast.makeText(context, "Access Denied", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }
            })
        biometricPrompt.authenticate(promptInfo)
    }
}
