package com.mustafa.biometricauthentication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mustafa.biometricauthentication.biometric_manager.BiometricPromptManager
import com.mustafa.biometricauthentication.biometric_manager.BiometricResults
import com.mustafa.biometricauthentication.ui.theme.BiometricAuthenticationTheme

class MainActivity : AppCompatActivity() {

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BiometricAuthenticationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val biometricResults by promptManager.promptResult.collectAsState(initial = null)

                    val enrollLauncher =
                        rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartActivityForResult(),
                            onResult = {
                                println("Activity result $it")
                            }
                        )


                    LaunchedEffect(key1 = biometricResults) {
                        if (biometricResults is BiometricResults.AuthenticationNotSet) {
                            if (Build.VERSION.SDK_INT >= 30) {
                                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                    putExtra(
                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                    )
                                }

                                enrollLauncher.launch(enrollIntent)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            promptManager.showBiometricManager(
                                "Sample prompt",
                                "Sample prompt description"
                            )
                        }) {
                            Text(text = "Authenticate")
                        }

                        biometricResults?.let { res ->
                            Text(
                                text = when (res) {
                                    is BiometricResults.AuthenticationError -> {
                                        res.error
                                    }

                                    BiometricResults.AuthenticationFailed -> {
                                        "Authentication Failed"
                                    }

                                    BiometricResults.AuthenticationNotSet -> {
                                        "Authentication Not Set"
                                    }

                                    BiometricResults.AuthenticationSuccess -> {
                                        "Authentication Success"
                                    }

                                    BiometricResults.FeatureUnavailable -> {
                                        "Feature Unavailable"

                                    }

                                    BiometricResults.HardwareUnavailable -> {
                                        "Hardware Unavailable"
                                    }
                                }
                            )

                        }
                    }
                }
            }
        }
    }
}
