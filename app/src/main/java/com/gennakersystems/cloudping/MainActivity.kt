package com.gennakersystems.cloudping

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gennakersystems.cloudping.ui.theme.CloudPingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CloudPingTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    val pingViewModel: PingViewModel = viewModel()
                    PingStatus(
                        uiState = pingViewModel.uiState,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun PingStatus(uiState: PingUiState, modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = modifier
                .safeDrawingPadding()
                .padding(16.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PublicIpCard(ip = uiState.publicIp, isLandscape = isLandscape)
                ProviderCard(provider = uiState.provider, isLandscape = isLandscape)
                PingCard(name = "Cloudflare", ip = uiState.cloudflareIp, ping = uiState.cloudflarePing, isLandscape = isLandscape)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InternalIpCard(ip = uiState.internalIp, isLandscape = isLandscape)
                PingCard(name = "Google", ip = uiState.googleIp, ping = uiState.googlePing, isLandscape = isLandscape)
                PingCard(name = "Gateway", ip = uiState.gatewayIp, ping = uiState.gatewayPing, isLandscape = isLandscape)
            }
        }
    } else {
        Column(
            modifier = modifier
                .safeDrawingPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PublicIpCard(ip = uiState.publicIp, isLandscape = isLandscape)
            ProviderCard(provider = uiState.provider, isLandscape = isLandscape)
            PingCard(name = "Cloudflare", ip = uiState.cloudflareIp, ping = uiState.cloudflarePing, isLandscape = isLandscape)
            PingCard(name = "Google", ip = uiState.googleIp, ping = uiState.googlePing, isLandscape = isLandscape)
            InternalIpCard(ip = uiState.internalIp, isLandscape = isLandscape)
            PingCard(name = "Gateway", ip = uiState.gatewayIp, ping = uiState.gatewayPing, isLandscape = isLandscape)
        }
    }
}

@Composable
fun ErrorCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        border = BorderStroke(2.dp, Color(0xFFFFA500)) // Orange
    ) {
        Box(
            modifier = Modifier.padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SignalWifiOff,
                contentDescription = "No connectivity",
                tint = Color.White
            )
        }
    }
}

@Composable
fun PingCard(name: String, ip: String, ping: String, modifier: Modifier = Modifier, isLandscape: Boolean = false) {
    val padding = if (isLandscape) 8.dp else 16.dp
    val titleSize = if (isLandscape) 16.sp else 20.sp
    val valueSize = if (isLandscape) 36.sp else 48.sp

    if (ping == "Error" || ping == "N/A") {
        ErrorCard()
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
        ) {
            Column(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$name ($ip)",
                    color = Color.White,
                    fontSize = titleSize,
                )
                Text(
                    text = ping,
                    color = Color.White,
                    fontSize = valueSize,
                )
            }
        }
    }
}

@Composable
fun InternalIpCard(ip: String, modifier: Modifier = Modifier, isLandscape: Boolean = false) {
    val padding = if (isLandscape) 8.dp else 16.dp
    val titleSize = if (isLandscape) 16.sp else 20.sp
    val valueSize = if (isLandscape) 36.sp else 48.sp

    if (ip == "Error" || ip == "N/A") {
        ErrorCard()
    } else {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .border(BorderStroke(2.dp, Color.Green), shape = CardDefaults.shape),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF212121)),
        ) {
            Column(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Internal IP",
                    color = Color.White,
                    fontSize = titleSize,
                )
                Text(
                    text = ip,
                    color = Color.White,
                    fontSize = valueSize,
                )
            }
        }
    }
}

@Composable
fun PublicIpCard(ip: String, modifier: Modifier = Modifier, isLandscape: Boolean = false) {
    val padding = if (isLandscape) 8.dp else 16.dp
    val titleSize = if (isLandscape) 16.sp else 20.sp
    val valueSize = if (isLandscape) 36.sp else 48.sp

    if (ip == "Error" || ip == "N/A") {
        ErrorCard()
    } else {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .border(BorderStroke(2.dp, Color.Green), shape = CardDefaults.shape),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF212121)),
        ) {
            Column(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Public IP",
                    color = Color.White,
                    fontSize = titleSize,
                )
                Text(
                    text = ip,
                    color = Color.White,
                    fontSize = valueSize,
                )
            }
        }
    }
}

@Composable
fun ProviderCard(provider: String, modifier: Modifier = Modifier, isLandscape: Boolean = false) {
    val padding = if (isLandscape) 8.dp else 16.dp
    val titleSize = if (isLandscape) 16.sp else 20.sp
    val valueSize = if (isLandscape) 28.sp else 24.sp

    if (provider == "Error" || provider == "N/A") {
        ErrorCard()
    } else {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .border(BorderStroke(2.dp, Color.Green), shape = CardDefaults.shape),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF212121)),
        ) {
            Column(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Provider",
                    color = Color.White,
                    fontSize = titleSize,
                )
                Text(
                    text = provider,
                    color = Color.White,
                    fontSize = valueSize,
                    maxLines = 2
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PingStatusPreview() {
    CloudPingTheme {
        PingStatus(
            uiState = PingUiState(
                cloudflareIp = "1.1.1.1",
                googleIp = "8.8.8.8",
                gatewayIp = "192.168.1.100",
                internalIp = "192.168.1.101",
                publicIp = "123.45.67.89",
                provider = "Starlink",
                cloudflarePing = "12 ms",
                googlePing = "34 ms",
                gatewayPing = "5 ms"
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}
