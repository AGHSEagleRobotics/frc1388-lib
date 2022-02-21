package com.eaglerobotics.lib.shuffleboard

import com.eaglerobotics.lib.WPILibWordSpec
import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

abstract class ShuffleboardWordSpec(
    body: ShuffleboardWordSpec.() -> Unit = {},
): WPILibWordSpec(clearNetworkTable = true) {
    lateinit var container: ShuffleboardContainer
    lateinit var containerNetworkTable: NetworkTable

    init {
        this.beforeTest {
            val tabName = it.descriptor.id.value

            container = Shuffleboard.getTab(tabName)
            containerNetworkTable = NetworkTableInstance.getDefault()
                .getTable(Shuffleboard.kBaseTableName)
                .getSubTable(tabName)
        }
        body()
    }
}