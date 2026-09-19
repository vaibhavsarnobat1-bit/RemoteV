package com.smartremote.pro.domain.usecases

import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.repository.DeviceRepository

class DiscoverNetworkDevicesUseCase(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(): List<Device> {
        return deviceRepository.discoverNetworkDevices()
    }
}
