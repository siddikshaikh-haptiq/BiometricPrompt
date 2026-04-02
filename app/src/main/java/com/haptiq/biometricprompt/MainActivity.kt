package com.haptiq.biometricprompt


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.haptiq.biometricprompt.ui.theme.BiometricPromptTheme

class MainActivity : AppCompatActivity() {
    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiometricPromptTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val biometricResult by promptManager.promptResults.collectAsState(
                        initial = null
                    )
                    val enrollLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = {
                            println("Activity result: $it")
                        }
                    )
                    LaunchedEffect(biometricResult) {
                        if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
                            if (Build.VERSION.SDK_INT >= 30) {
                                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                    putExtra(
                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
                                    )
                                }
                                enrollLauncher.launch(enrollIntent)
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            promptManager.showBiometricPrompt(
                                title = "Sample prompt",
                                description = "Sample prompt description"
                            )
                        }) {
                            Text(text = "Authenticate")
                        }
                        biometricResult?.let { result ->
                            Text(
                                text = when (result) {
                                    is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                                        result.error
                                    }

                                    BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                                        "Authentication failed"
                                    }

                                    BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                                        "Biometric authentication not set"
                                    }

                                    BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                                        "Authentication success"
                                    }

                                    BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                                        "Feature unavailable"
                                    }

                                    BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                                        "Hardware unavailable"
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BiometricPromptTheme {
        Greeting("Android")
    }
}
