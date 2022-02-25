package com.eaglerobotics.lib.shuffleboard

import com.eaglerobotics.lib.WPILibWordSpec
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.PS4Controller
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.shuffleboard.shouldHaveProperty
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize

class InputActionBindingsTest : WPILibWordSpec({
    // These are lazy so that they don't get created until after HAL.initialize has been called
    val xboxController by lazy { XboxController(0) }

    val ps4Controller by lazy { PS4Controller(1) }

    "InputActionBindings constructor" should {
        "create Controls tab" {
            InputActionBindings(
                AxisActions::class.java,
                ButtonActions::class.java,
                xboxController,
                ps4Controller
            )

            val networkTable = NetworkTableInstance.getDefault().getTable(Shuffleboard.kBaseTableName)
            networkTable.subTables shouldContain "Controls"

            val tab = Shuffleboard.getTab("Controls")
            assertSoftly(tab.components) {
                shouldHaveSingleElement { it.title == "Axis Bindings" }
                shouldHaveSingleElement { it.title == "Button Bindings" }
            }

            assertSoftly(tab.getLayout("Axis Bindings")) {
                shouldHaveProperty("Label Position" to "TOP")
                components shouldHaveSize AxisActions.values().size
            }

            assertSoftly(tab.getLayout("Button Bindings")) {
                shouldHaveProperty("Label Position" to "TOP")
                components shouldHaveSize ButtonActions.values().size
            }
        }
    }
})
