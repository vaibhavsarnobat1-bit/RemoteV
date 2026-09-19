package com.smartremote.pro

import com.smartremote.pro.core.ir.IRTransmitter
import com.smartremote.pro.core.macro.MacroExecutionEngine
import com.smartremote.pro.core.network.SmartTvClient
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.models.DeviceType
import com.smartremote.pro.domain.models.Macro
import com.smartremote.pro.domain.models.MacroStep
import com.smartremote.pro.domain.models.RemoteCommand
import com.smartremote.pro.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MacroExecutionEngineTest {

    private lateinit var fakeDeviceRepository: DeviceRepository
    private lateinit var fakeIrTransmitter: IRTransmitter
    private lateinit var fakeSmartTvClient: SmartTvClient
    private lateinit var macroEngine: MacroExecutionEngine

    @Before
    fun setup() {
        val testDevice = Device(
            id = "tv_test",
            name = "Test TV",
            type = DeviceType.TV,
            brand = "Samsung"
        )

        fakeDeviceRepository = object : DeviceRepository {
            override fun getAllDevices(): Flow<List<Device>> = flowOf(listOf(testDevice))
            override fun getDevicesByRoom(room: String): Flow<List<Device>> = flowOf(listOf(testDevice))
            override suspend fun getDeviceById(id: String): Device? = testDevice
            override suspend fun insertDevice(device: Device): Long = 1L
            override suspend fun updateDevice(device: Device) {}
            override suspend fun deleteDevice(id: String) {}
            override suspend fun getCommandForDevice(deviceId: String, commandName: String): RemoteCommand? =
                RemoteCommand(id = "cmd_1", name = commandName, hexCode = "0x20DF10EF")
            override suspend fun getPreloadedCommands(brand: String, deviceType: DeviceType): Map<String, String> =
                mapOf("power" to "0x20DF10EF", "volUp" to "0x20DF40BF")
            override suspend fun discoverNetworkDevices(): List<Device> = listOf(testDevice)
        }

        fakeSmartTvClient = SmartTvClient()
        // Simulated transmitter instance
        fakeIrTransmitter = IRTransmitter(android.content.ContextWrapper(null))
        macroEngine = MacroExecutionEngine(fakeDeviceRepository, fakeIrTransmitter, fakeSmartTvClient)
    }

    @Test
    fun execute_macroWithTwoSteps_callsProgressCallback() = runTest {
        val step1 = MacroStep(id = "s1", macroId = "m1", stepOrder = 1, targetDeviceId = "tv_test", commandName = "power", delayAfterMs = 10L)
        val step2 = MacroStep(id = "s2", macroId = "m1", stepOrder = 2, targetDeviceId = "tv_test", commandName = "volUp", delayAfterMs = 10L)
        val macro = Macro(id = "m1", name = "Test Routine", description = "Test", steps = listOf(step1, step2))

        var progressCount = 0
        val success = macroEngine.execute(macro) { current, total ->
            progressCount = current
            assertEquals(2, total)
        }

        assertTrue(success)
        assertEquals(2, progressCount)
    }
}
