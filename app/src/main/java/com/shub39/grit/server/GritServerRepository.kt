package com.shub39.grit.server

import kotlinx.coroutines.flow.StateFlow

interface GritServerRepository {
    val isRunning: StateFlow<Boolean>
    val serverUrl: StateFlow<String?>
    val serverPort: StateFlow<Int>

    suspend fun startServer(port: Int)
    fun stopServer()
    suspend fun setServerPort(port: Int)
}