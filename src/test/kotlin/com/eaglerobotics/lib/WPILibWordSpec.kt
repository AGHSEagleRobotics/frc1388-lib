package com.eaglerobotics.lib

import edu.wpi.first.hal.HAL
import edu.wpi.first.networktables.NetworkTableInstance
import io.kotest.core.spec.style.WordSpec

abstract class WPILibWordSpec(
    body: WPILibWordSpec.() -> Unit = {},
    clearNetworkTable: Boolean = true,
): WordSpec() {
    init {
        this.beforeTest {
            HAL.initialize(500, 0)

            if (clearNetworkTable) {
                NetworkTableInstance.getDefault().deleteAllEntries()
            }
        }
        body()
    }
}