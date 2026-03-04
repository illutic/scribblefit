package com.scribblefit.core.network

import javax.inject.Inject
import javax.inject.Singleton

interface NetworkConfig {
    val baseUrl: String
}

@Singleton
class NetworkConfigImpl @Inject constructor() : NetworkConfig {
    override val baseUrl: String = "http://10.0.2.2:8080/"
}
