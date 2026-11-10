package com.remoteview

import android.app.Activity
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.remoteview.capture.ScreenCaptureService
import android.content.Intent
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private val screenConsent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val intent = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, result.resultCode)
                putExtra(ScreenCaptureService.EXTRA_RESULT_DATA, result.data)
            }
            ContextCompat.startForegroundService(this, intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var mode by remember { mutableStateOf("Receiver") }
                var code by remember { mutableStateOf("") }
                var status by remember { mutableStateOf("Not connected") }
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        Text("RemoteView", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Text("Secure remote assistance • Android")
                        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                            listOf("Controller", "Receiver").forEachIndexed { index, item ->
                                SegmentedButton(selected = mode == item, onClick = { mode = item }, shape = SegmentedButtonDefaults.itemShape(index, 2)) { Text(item) }
                            }
                        }
                        if (mode == "Receiver") {
                            Text("Share this phone", style = MaterialTheme.typography.titleLarge)
                            Text("Start sharing only when you are ready. Android will ask you to approve screen capture.")
                            Button(onClick = {
                                val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                                screenConsent.launch(manager.createScreenCaptureIntent())
                            }, modifier = Modifier.fillMaxWidth()) { Text("Allow screen sharing") }
                        } else {
                            Text("Connect to another phone", style = MaterialTheme.typography.titleLarge)
                            OutlinedTextField(value = code, onValueChange = { code = it.take(8).uppercase() }, label = { Text("Pairing code") }, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters), modifier = Modifier.fillMaxWidth())
                            Button(onClick = { status = "Pairing server is not configured yet" }, modifier = Modifier.fillMaxWidth(), enabled = code.length >= 6) { Text("Connect") }
                        }
                        Text("Session: $status", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.weight(1f))
                        Text("A visible notification and the phone owner's permission are required. Camera and remote-control features are not enabled in this build.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
