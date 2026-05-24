package com.example.data.model

data class WifiScanResult(
    val ssid: String,
    val connectionType: String, // Wi-Fi / Cellular / Ethernet
    val encryptionType: String, // WPA3, WPA2, WEP, Open
    val status: SecurityStatus, // SAFE, RISKY, UNSAFE
    val score: Int, // 0 - 100
    val dnsInfo: String,
    val gatewayIp: String,
    
    // Cyber Threat Indicators
    val isOpen: Boolean = false,
    val weakEncryption: Boolean = false,
    val suspiciousDns: Boolean = false,
    val captivePortalDetected: Boolean = false,
    val mitmIndicator: Boolean = false,
    val evilTwinDetected: Boolean = false,
    val arpSpoofingDetected: Boolean = false,
    val httpsDowngradeDetected: Boolean = false,
    val suspiciousGateway: Boolean = false
) {
    val statusText: String
        get() = when (status) {
            SecurityStatus.SAFE -> "Safe"
            SecurityStatus.RISKY -> "Risky"
            SecurityStatus.UNSAFE -> "Unsafe"
        }
}

enum class SecurityStatus {
    SAFE,
    RISKY,
    UNSAFE
}
