package com.eaglerobotics.lib.shuffleboard

import com.eaglerobotics.lib.shuffleboard.internal.OISubsystem
import edu.wpi.first.hal.HAL
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget
import edu.wpi.first.wpilibj.shuffleboard.shouldContainAllProperties
import edu.wpi.first.wpilibj.simulation.XboxControllerSim
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearAllMocks
import io.mockk.spyk

private class ActionBindingTestImpl<T : Action>(
    axis: T,
    oi: OISubsystem,
    container: ShuffleboardContainer
) : ActionBinding<T, Any?>(axis, oi, container) {
    override fun get(): Any? {
        TODO("Not yet implemented")
    }

    override fun getChannelName(channel: Int): String = "$channel"

    override fun getSelectedChannel(joystick: GenericHID?): Int {
        TODO("Not yet implemented")
    }
}

class ActionBindingTest : WordSpec({
    lateinit var container: ShuffleboardContainer

    val driverController by lazy { XboxController(0) }
    val driverControllerSim by lazy { XboxControllerSim(driverController) }
    val opController by lazy { XboxController(1) }
    val opControllerSim by lazy { XboxControllerSim(opController) }

    val oi by lazy { spyk(OISubsystem(driverController, opController)) }

    beforeTest {
        HAL.initialize(500, 0)
        NetworkTableInstance.getDefault().deleteAllEntries()
        container = Shuffleboard.getTab(it.name.testName)

        clearAllMocks()
    }
    "ActionBinding constructor" should {
        "create shuffleboard grid" {
            ActionBindingTestImpl(ButtonActions.SHOOT, oi, container)

            container.components.shouldHaveSingleElement { it is ShuffleboardLayout }

            val grid = container.components[0] as ShuffleboardLayout

            grid shouldContainAllProperties mapOf(
                "Number of Columns" to 2,
                "Number of Rows" to 1,
                "Label position" to "HIDDEN"
            )

            grid.components shouldHaveSize 2

            assertSoftly(grid.components[0]) {
                this.shouldBeInstanceOf<SimpleWidget>()
                title shouldBe "Bind"
                type shouldBe BuiltInWidgets.kToggleButton.widgetName
                entry.value.boolean shouldBe false
            }

            assertSoftly(grid.components[1]) {
                this.shouldBeInstanceOf<SuppliedValueWidget<String>>()
                // TODO find good a way to actually test the value being displayed
            }
        }

        "bind to default value" {
            val binding = ActionBindingTestImpl(ButtonActions.SHOOT, oi, container)

            binding.boundChannel shouldBe binding.action.defaultChannel
            binding.boundController.port shouldBe binding.action.defaultPort
        }
    }

    "ActionBinding.bindto" should {
        "update binding" {
            val binding = ActionBindingTestImpl(ButtonActions.SHOOT, oi, container)

            binding.bindTo(opController, 5)

            binding.boundChannel shouldBe 5
            binding.boundController shouldBe opController
        }
    }

    ""
})
