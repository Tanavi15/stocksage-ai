package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    apiKey: String,
    onSaveApiKey: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var keyInput by remember(apiKey) { mutableStateOf(apiKey) }
    var realTimeStream by remember { mutableStateOf(true) }
    var priceTargetAlerts by remember { mutableStateOf(true) }
    var darkTerminalTheme by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Settings Header
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = CyanPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("TERMINAL CONFIGURATION & API KEYS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                        Text("Configure Gemini 2.5 AI keys and streaming data feeds", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }
        }

        // Gemini API Key Input
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("GOOGLE GEMINI API KEY", fontWeight = FontWeight.Bold, color = GoldAccent, fontSize = 13.sp)
                    Text("Enter your custom key to unlock high-rate limit Gemini 2.5 Flash market reasoning.", fontSize = 11.sp, color = TextMuted)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { keyInput = it },
                        label = { Text("Gemini API Key") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("api_key_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onSaveApiKey(keyInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        modifier = Modifier.testTag("save_api_key_button")
                    ) {
                        Text("Save & Connect Key", color = DarkSurface, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Stream Toggles
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("TERMINAL PREFERENCES", fontWeight = FontWeight.Bold, color = CyanPrimary, fontSize = 13.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Real-time Tick Data Stream", fontSize = 13.sp, color = TextPrimary)
                            Text("Simulate live Orderbook updates", fontSize = 10.sp, color = TextMuted)
                        }
                        Switch(
                            checked = realTimeStream,
                            onCheckedChange = { realTimeStream = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Price Target Push Alerts", fontSize = 13.sp, color = TextPrimary)
                            Text("Notify on breakout levels", fontSize = 10.sp, color = TextMuted)
                        }
                        Switch(
                            checked = priceTargetAlerts,
                            onCheckedChange = { priceTargetAlerts = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("High Contrast Bloomberg Dark Theme", fontSize = 13.sp, color = TextPrimary)
                            Text("Optimized for low-light trading desks", fontSize = 10.sp, color = TextMuted)
                        }
                        Switch(
                            checked = darkTerminalTheme,
                            onCheckedChange = { darkTerminalTheme = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                        )
                    }
                }
            }
        }
    }
}
