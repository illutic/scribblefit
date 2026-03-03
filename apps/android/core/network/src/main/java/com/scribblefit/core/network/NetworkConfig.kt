package com.scribblefit.core.network

import javax.inject.Inject
import javax.inject.Singleton

interface NetworkConfig {
    val baseUrl: String
}

@Singleton
class NetworkConfigImpl @Inject constructor() : NetworkConfig {
    // In a real app, this could come from BuildConfig, local.properties, or a remote config
    override val baseUrl: String = "http://10.0.2.2:8080/"
}
