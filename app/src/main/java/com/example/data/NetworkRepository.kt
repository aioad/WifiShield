package com.example.data

import com.example.data.database.NetworkDao
import com.example.data.database.NetworkHistory
import com.example.data.database.TrustedNetwork
import kotlinx.coroutines.flow.Flow

class NetworkRepository(private val networkDao: NetworkDao) {

    val trustedNetworks: Flow<List<TrustedNetwork>> = networkDao.getAllTrustedNetworks()
    val networkHistory: Flow<List<NetworkHistory>> = networkDao.getNetworkHistory()

    suspend fun addTrustedNetwork(ssid: String, type: String) {
        val network = TrustedNetwork(ssid = ssid, type = type)
        networkDao.insertTrustedNetwork(network)
    }

    suspend fun removeTrustedNetwork(id: Int) {
        networkDao.deleteTrustedNetworkById(id)
    }

    suspend fun removeTrustedNetworkBySsid(ssid: String) {
        networkDao.deleteTrustedNetworkBySsid(ssid)
    }

    suspend fun isNetworkTrusted(ssid: String): Boolean {
        return networkDao.isNetworkTrusted(ssid)
    }

    suspend fun addHistoryItem(
        ssid: String,
        status: String,
        score: Int,
        encryptionType: String,
        vpnUsed: Boolean
    ) {
        val item = NetworkHistory(
            ssid = ssid,
            securityStatus = status,
            securityScore = score,
            encryptionType = encryptionType,
            vpnUsed = vpnUsed
        )
        networkDao.insertHistoryItem(item)
    }

    suspend fun clearHistory() {
        networkDao.clearHistory()
    }
}
