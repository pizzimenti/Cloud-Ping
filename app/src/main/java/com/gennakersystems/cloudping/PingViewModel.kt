package com.gennakersystems.cloudping

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

@Serializable
data class IpInfo(
    val ip: String,
    val org: String? = null
)

data class PingUiState(
    val cloudflareIp: String = "1.1.1.1",
    val googleIp: String = "8.8.8.8",
    val gatewayIp: String = "Pinging...",
    val internalIp: String = "Pinging...",
    val publicIp: String = "Fetching...",
    val provider: String = "Fetching...",
    val cloudflarePing: String = "Pinging...",
    val googlePing: String = "Pinging...",
    val gatewayPing: String = "Pinging..."
)

class PingViewModel(application: Application) : AndroidViewModel(application) {
    var uiState by mutableStateOf(PingUiState())
        private set

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val networkMonitor = NetworkMonitor(application)

    init {
        viewModelScope.launch {
            networkMonitor.isConnected
                .distinctUntilChanged()
                .collect { connected ->
                    if (connected) {
                        startPinging()
                    } else {
                        uiState = PingUiState(
                            publicIp = "Error",
                            provider = "Error",
                            internalIp = "Error",
                            gatewayIp = "Error",
                            cloudflarePing = "Error",
                            googlePing = "Error",
                            gatewayPing = "Error"
                        )
                    }
                }
        }
    }

    private fun fetchInternalIp() {
        viewModelScope.launch {
            val internalIp = getInternalIp(getApplication())
            uiState = uiState.copy(internalIp = internalIp)
        }
    }

    private fun fetchNetworkInfo() {
        viewModelScope.launch {
            val ipInfo: IpInfo? = try {
                withContext(Dispatchers.IO) {
                    client.get("https://ipinfo.io/json").body<IpInfo>()
                }
            } catch (e: Exception) {
                Log.e("PingViewModel", "Error fetching network info", e)
                null
            }

            uiState = if (ipInfo != null) {
                uiState.copy(
                    publicIp = ipInfo.ip,
                    provider = ipInfo.org ?: "N/A"
                )
            } else {
                uiState.copy(
                    publicIp = "Error",
                    provider = "Error"
                )
            }
        }
    }

    private fun startPinging() {
        viewModelScope.launch {
            var networkInfoCounter = 0
            while (true) {
                if (networkInfoCounter == 0) {
                    fetchNetworkInfo()
                    fetchInternalIp()
                }
                networkInfoCounter = (networkInfoCounter + 1) % 30

                val gatewayIp = getGatewayIp(getApplication())
                uiState = uiState.copy(gatewayIp = gatewayIp)

                val cloudflare = ping(uiState.cloudflareIp)
                val google = ping(uiState.googleIp)
                val gateway = if (gatewayIp != "N/A") ping(gatewayIp, withDecimal = true) else "N/A"

                uiState = uiState.copy(
                    cloudflarePing = cloudflare,
                    googlePing = google,
                    gatewayPing = gateway
                )
                delay(1000)
            }
        }
    }

    private suspend fun ping(host: String, withDecimal: Boolean = false): String {
        return withContext(Dispatchers.IO) {
            try {
                val process = ProcessBuilder("ping", "-c", "1", host).start()
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var pingTime = "N/A"
                
                reader.use { r ->
                    var line = r.readLine()
                    while (line != null) {
                        if (line.contains("time=")) {
                            val timeValue = line.split("time=")[1].split(" ")[0]
                            pingTime = if (withDecimal) {
                                val parts = timeValue.split(".")
                                if (parts.size > 1) {
                                    "${parts[0]}.${parts[1].take(1)} ms"
                                } else {
                                    "$timeValue ms"
                                }
                            } else {
                                "${timeValue.split(".")[0]} ms"
                            }
                            break
                        }
                        line = r.readLine()
                    }
                }
                process.waitFor()
                pingTime
            } catch (e: Exception) {
                Log.e("PingViewModel", "Error pinging $host", e)
                "Error"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}
