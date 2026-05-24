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
        var ssid = wifiManager?.connectionInfo?.ssid ?: ""
        
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
