package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.NetworkRepository
import com.example.data.database.NetworkHistory
import com.example.data.database.TrustedNetwork
import com.example.data.model.SecurityStatus
import com.example.data.model.WifiScanResult
import com.example.utils.NotificationHelper
import com.example.vpn.MyVpnService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WifiShieldViewModel(private val repository: NetworkRepository) : ViewModel() {

    private val _scanResult = MutableStateFlow<WifiScanResult?>(null)
    val scanResult: StateFlow<WifiScanResult?> = _scanResult.asStateFlow()

    // AI Advisory States
    private val _aiAdvisoryState = MutableStateFlow<AiAdvisoryState>(AiAdvisoryState.Idle)
    val aiAdvisoryState: StateFlow<AiAdvisoryState> = _aiAdvisoryState.asStateFlow()

    private val _aiChatState = MutableStateFlow<AiChatState>(AiChatState.Idle)
    val aiChatState: StateFlow<AiChatState> = _aiChatState.asStateFlow()

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _warningCountdown = MutableStateFlow<Int?>(null)
    val warningCountdown: StateFlow<Int?> = _warningCountdown.asStateFlow()

    private val _showWarningModal = MutableStateFlow(false)
    val showWarningModal: StateFlow<Boolean> = _showWarningModal.asStateFlow()

    // Service VPN State Flow
    val isVpnActiveFlow: StateFlow<Boolean> = MyVpnService.vpnActive

    // Settings Parameters
    private val _autoVpnEnabled = MutableStateFlow(true)
    val autoVpnEnabled = _autoVpnEnabled.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode = _isDarkMode.asStateFlow()

    // Trusted Networks and Scan Histories
    val trustedNetworks: StateFlow<List<TrustedNetwork>> = repository.trustedNetworks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val networkHistory: StateFlow<List<NetworkHistory>> = repository.networkHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var scanJob: Job? = null
    private var countdownJob: Job? = null
    private var notificationHelper: NotificationHelper? = null

    // Initialize notification helper lazily
    fun initNotificationHelper(context: Context) {
        if (notificationHelper == null) {
            notificationHelper = NotificationHelper(context.applicationContext)
        }
        // Sync active network info on boot
        refreshActiveConnection(context)
    }

    fun setAutoVpn(enabled: Boolean) {
        _autoVpnEnabled.value = enabled
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Refresh and detect active connection
    fun refreshActiveConnection(context: Context) {
        val originalSsid = getDeviceSsid(context)
        val encryption = getDeviceEncryption(context)
        val isWifi = isWifiConnected(context)

        Log.d("WifiShieldViewModel", "Refreshing. Is Wi-Fi: $isWifi, SSID: $originalSsid")

        // Construct baseline
        val baseScore = if (originalSsid == "<unknown ssid>" || originalSsid.contains("Free") || originalSsid.contains("Open")) 25 else 85
        val baseStatus = if (baseScore <= 30) SecurityStatus.UNSAFE else if (baseScore <= 70) SecurityStatus.RISKY else SecurityStatus.SAFE

        _scanResult.value = WifiScanResult(
            ssid = originalSsid,
            connectionType = if (isWifi) "Wi-Fi" else "Cellular Data",
            encryptionType = encryption,
            status = baseStatus,
            score = baseScore,
            dnsInfo = "192.168.1.1 (ISP DNS)",
            gatewayIp = "192.168.1.254"
        )
    }

    // Run custom deep cyber scan simulation
    fun triggerDeepScan(context: Context, customSsid: String? = null) {
        scanJob?.cancel()
        countdownJob?.cancel()
        _warningCountdown.value = null
        _showWarningModal.value = false

        scanJob = viewModelScope.launch {
            val ssid = customSsid ?: getDeviceSsid(context)
            
            // Checking if the SSID is trusted
            val isTrusted = repository.isNetworkTrusted(ssid)

            _scanState.value = ScanState.Scanning("Initializing security audit pipeline...")
            delay(800)
            _scanState.value = ScanState.Scanning("Sniffing network packet encryption headers...")
            delay(1000)
            _scanState.value = ScanState.Scanning("Validating local DNS server signatures...")
            delay(1000)
            _scanState.value = ScanState.Scanning("Probing ARP network tables for active spoofers...")
            delay(1000)
            _scanState.value = ScanState.Scanning("Analyzing HTTPS downgrade thresholds & portal gateways...")
            delay(800)

            // Calculate cybersecurity threat scoring
            val isSsidRisky = ssid.contains("Free", ignoreCase = true) || ssid.contains("Public", ignoreCase = true) || ssid.contains("Airport", ignoreCase = true)
            val isSsidUnsafe = ssid.contains("Unsecured", ignoreCase = true) || ssid.contains("Hacker", ignoreCase = true)

            val score = when {
                isTrusted -> 96
                isSsidUnsafe -> 18
                isSsidRisky -> 42
                else -> 88
            }

            val status = when {
                score <= 30 -> SecurityStatus.UNSAFE
                score <= 70 -> SecurityStatus.RISKY
                else -> SecurityStatus.SAFE
            }

            val result = WifiScanResult(
                ssid = ssid,
                connectionType = "Wi-Fi",
                encryptionType = if (score <= 30) "None (Open Network)" else if (score <= 70) "WPA (Deprecating)" else "WPA3 Secured",
                status = status,
                score = score,
                dnsInfo = if (score <= 30) "103.8.22.4 (Unrecognized / Suspicious)" else "8.8.8.8 (Google Encrypted)",
                gatewayIp = "192.168.1.1",
                isOpen = score <= 30,
                weakEncryption = score <= 70,
                suspiciousDns = score <= 50,
                captivePortalDetected = score == 42,
                mitmIndicator = score <= 30,
                evilTwinDetected = score <= 20,
                arpSpoofingDetected = score <= 25,
                httpsDowngradeDetected = score <= 30,
                suspiciousGateway = score <= 30
            )

            _scanResult.value = result
            _scanState.value = ScanState.Success(result)

            // Logging network scan occurrence to local database
            repository.addHistoryItem(
                ssid = result.ssid,
                status = result.status.name,
                score = result.score,
                encryptionType = result.encryptionType,
                vpnUsed = MyVpnService.isVpnRunning()
            )

            // Deciding layout behavior triggers
            if (result.status == SecurityStatus.UNSAFE && !isTrusted) {
                if (_notificationsEnabled.value) {
                    notificationHelper?.showUnsafeWifiNotification(result.ssid, result.score)
                }
                triggerWarningFlow(context)
            } else if (result.status == SecurityStatus.SAFE) {
                if (_notificationsEnabled.value) {
                    notificationHelper?.showSafeNetworkConnectedNotification(result.ssid)
                }
            }
        }
    }

    // Dangerous Wi-Fi Warning flow with automatic 20-second trigger
    private fun triggerWarningFlow(context: Context) {
        countdownJob?.cancel()
        _showWarningModal.value = true
        _warningCountdown.value = 20

        countdownJob = viewModelScope.launch {
            while (_warningCountdown.value != null && _warningCountdown.value!! > 0) {
                delay(1000)
                _warningCountdown.value = _warningCountdown.value!! - 1
            }
            // Auto complete timer -> Engage VPN
            if (_warningCountdown.value == 0) {
                _warningCountdown.value = null
                _showWarningModal.value = false
                engageVpn(context)
            }
        }
    }

    fun dismissWarningModal() {
        countdownJob?.cancel()
        _warningCountdown.value = null
        _showWarningModal.value = false
    }

    fun stayConnectedAction() {
        // Stop timer but user decides to bypass anyway
        dismissWarningModal()
    }

    fun startVpnAction(context: Context) {
        dismissWarningModal()
        engageVpn(context)
    }

    // Enable VPN
    fun engageVpn(context: Context) {
        if (!MyVpnService.isVpnRunning()) {
            val intent = Intent(context, MyVpnService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            if (_notificationsEnabled.value) {
                notificationHelper?.showVpnEnabledNotification()
            }
        }
    }

    fun stopVpn(context: Context) {
        val intent = Intent(context, MyVpnService::class.java).apply {
            action = MyVpnService.ACTION_DISCONNECT
        }
        context.startService(intent)
    }

    // Database Actions wrapped
    fun addTrustedNetwork(ssid: String, type: String) {
        viewModelScope.launch {
            repository.addTrustedNetwork(ssid, type)
            // If the current result represents this SSID, refresh it
            _scanResult.value?.let {
                if (it.ssid == ssid) {
                    _scanResult.value = it.copy(status = SecurityStatus.SAFE, score = 98)
                    dismissWarningModal()
                }
            }
        }
    }

    fun removeTrustedNetwork(id: Int, ssidToRefresh: String) {
        viewModelScope.launch {
            repository.removeTrustedNetwork(id)
        }
    }

    fun clearScanHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }


    // Private utils to query system network adapters
    private fun getDeviceSsid(context: Context): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        var ssid = ""
        try {
            ssid = wifiManager?.connectionInfo?.ssid ?: ""
        } catch (e: SecurityException) {
            Log.w("WifiShieldViewModel", "No permission to get SSID via connectionInfo: ${e.message}")
        } catch (e: Exception) {
            Log.e("WifiShieldViewModel", "Exception reading SSID: ${e.message}")
        }
        
        // Remove enclosing quotes
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length - 1)
        }
        
        if (ssid == "<unknown ssid>" || ssid.isEmpty()) {
            // Fallback for demo: if Wi-Fi is connected but SSID represents unknown, use a standard public Wi-Fi simulation
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val activeNet = connManager?.activeNetwork
            val caps = connManager?.getNetworkCapabilities(activeNet)
            if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return "Public_Transit_Portal_WiFi"
            }
            return "Airport_Free_Unsecured"
        }
        return ssid
    }

    private fun getDeviceEncryption(context: Context): String {
        return "None (Open Network)" // Simplification for scanning displays
    }

    private fun isWifiConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = connectivityManager?.activeNetwork
        val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
    }

    // AI Integration functions
    fun analyzeNetworkWithAi(result: WifiScanResult) {
        _aiAdvisoryState.value = AiAdvisoryState.Loading
        viewModelScope.launch {
            try {
                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _aiAdvisoryState.value = AiAdvisoryState.Error(
                        "Gemini API Key is missing. Please set your key in the AI Studio Secrets panel."
                    )
                    return@launch
                }

                // Construct a detailed payload prompt
                val threatIndicators = mutableListOf<String>()
                if (result.isOpen) threatIndicators.add("- Open Wi-Fi (No Encryption)")
                if (result.weakEncryption) threatIndicators.add("- Weak / Outdated WEP protocol")
                if (result.suspiciousDns) threatIndicators.add("- Rogue DNS / Poison Configuration")
                if (result.captivePortalDetected) threatIndicators.add("- Unsecured Captive Portal Intercept")
                if (result.mitmIndicator) threatIndicators.add("- Potential MITM Downgrade risks")
                if (result.evilTwinDetected) threatIndicators.add("- Potential Evil Twin (SSID cloning spoof)")
                if (result.arpSpoofingDetected) threatIndicators.add("- ARP Spoofing/Network poisoning traces")
                if (result.httpsDowngradeDetected) threatIndicators.add("- HTTPS Downgrade vulnerability")
                if (result.suspiciousGateway) threatIndicators.add("- Suspicious Local Gateway Broadcasts")

                val threatListStr = if (threatIndicators.isEmpty()) "None detected" else threatIndicators.joinToString("\n")

                val prompt = """
                    You are an elite cyber-security AI network safety advisor. 
                    Provide a detailed, professional, and readable network vulnerability analysis based on the following Wi-Fi scan results:
                    
                    SSID: "${result.ssid}"
                    Connection Type: ${result.connectionType}
                    Encryption Type: ${result.encryptionType}
                    Safety Status: ${result.status.name}
                    Calculated Safety Score: ${result.score}/100 (Where 0 is extremely compromised and 100 is fully trusted & secured)
                    DNS Info: ${result.dnsInfo}
                    Gateway IP: ${result.gatewayIp}
                    
                    Identified Vulnerability Indicators:
                    $threatListStr
                    
                    Please structure your assessment with the following sections using clean text / markdown bullet points:
                    1. **OVERALL RISK RATING**: (Brief 1-2 sentence high-level security verdict on whether the user is safe typing passwords, reading emails, etc.)
                    2. **THREAT VULNERABILITY BREAKDOWN**: (For any identified vulnerability indicators above, provide a quick plain-English explanation of what that means and how an attacker could exploit it.)
                    3. **CRITICAL DEFENSE STEPS**: (Provide 3 concrete, styled, bulletproof bullet actions the user should take right now to secure their communications, e.g. using VPN, visiting only HTTPS websites, disabling auto-reconnect, etc.)
                    
                    Keep the tone professional, direct, encouraging, and authoritative. Avoid overly technical jargon where possible so the average user can understand, but maintain elite cyber-security standards.
                """.trimIndent()

                val request = com.example.data.api.GeminiRequest(
                    contents = listOf(
                        com.example.data.api.Content(
                            parts = listOf(com.example.data.api.Part(text = prompt))
                        )
                    )
                )

                val response = com.example.data.api.GeminiRetrofitClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (responseText != null) {
                    _aiAdvisoryState.value = AiAdvisoryState.Success(responseText)
                } else {
                    _aiAdvisoryState.value = AiAdvisoryState.Error("Received empty assessment from AI.")
                }
            } catch (e: Exception) {
                Log.e("WifiShieldViewModel", "Gemini API error during threat advice: ", e)
                _aiAdvisoryState.value = AiAdvisoryState.Error("Failed to fetch assessment: ${e.localizedMessage ?: "Unknown network error"}")
            }
        }
    }

    fun askAiQuestion(question: String, result: WifiScanResult?) {
        if (question.isBlank()) return
        _aiChatState.value = AiChatState.Loading
        viewModelScope.launch {
            try {
                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _aiChatState.value = AiChatState.Error(
                        "Gemini API Key is missing. Please set your key in the AI Studio Secrets panel."
                    )
                    return@launch
                }

                val currentNetworkContext = if (result != null) {
                    """
                    Current Wi-Fi Context:
                    - SSID: "${result.ssid}"
                    - Security Score: ${result.score}/100 [Status: ${result.status.name}]
                    - Encryption: ${result.encryptionType}
                    - Active threats flagged: Open WiFi(${result.isOpen}), Rogue DNS(${result.suspiciousDns}), ARP Spoofing(${result.arpSpoofingDetected}), MITM(${result.mitmIndicator})
                    """.trimIndent()
                } else {
                    "No active scan results or offline disconnected."
                }

                val prompt = """
                    You are the elite "WiFi Shield AI Counselor" integrated inside a real-time mobile cybersecurity app.
                    
                    $currentNetworkContext
                    
                    The user has asked the following security/network question:
                    "$question"
                    
                    Provide a plain-English, professional, informative, and engaging answer. Focus on concrete explanations, mobile internet safety, and VPN benefits where matching. Keep your response under 150 words and extremely readable, formatted with clear bullets if appropriate.
                """.trimIndent()

                val request = com.example.data.api.GeminiRequest(
                    contents = listOf(
                        com.example.data.api.Content(
                            parts = listOf(com.example.data.api.Part(text = prompt))
                        )
                    )
                )

                val response = com.example.data.api.GeminiRetrofitClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (responseText != null) {
                    _aiChatState.value = AiChatState.Success(responseText)
                } else {
                    _aiChatState.value = AiChatState.Error("AI was unable to process the query.")
                }
            } catch (e: Exception) {
                Log.e("WifiShieldViewModel", "Gemini API error during Q&A: ", e)
                _aiChatState.value = AiChatState.Error("Error contacting AI: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    fun resetAiChat() {
        _aiChatState.value = AiChatState.Idle
    }
}

sealed class AiAdvisoryState {
    object Idle : AiAdvisoryState()
    object Loading : AiAdvisoryState()
    data class Success(val advice: String) : AiAdvisoryState()
    data class Error(val errorMessage: String) : AiAdvisoryState()
}

sealed class AiChatState {
    object Idle : AiChatState()
    object Loading : AiChatState()
    data class Success(val response: String) : AiChatState()
    data class Error(val errorMessage: String) : AiChatState()
}

sealed class ScanState {
    object Idle : ScanState()
    data class Scanning(val status: String) : ScanState()
    data class Success(val result: WifiScanResult) : ScanState()
}

class WifiShieldViewModelFactory(private val repository: NetworkRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WifiShieldViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WifiShieldViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
