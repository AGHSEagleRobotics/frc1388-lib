package com.eaglerobotics.lib.shuffleboard

import com.eaglerobotics.lib.shuffleboard.internal.OISubsystem
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget
import edu.wpi.first.wpilibj.shuffleboard.shouldContainAllProperties
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

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

class ActionBindingTest : ShuffleboardWordSpec({
    // These are lazy so that they don't get created until after HAL.initialize has been called
    val driverController by lazy { XboxController(0) }
    val opController by lazy { XboxController(1) }

    val oi by lazy { OISubsystem(driverController, opController) }

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

    "ActionBinding.bindTo" should {
        "update binding" {
            val binding = ActionBindingTestImpl(ButtonActions.SHOOT, oi, container)

            binding.bindTo(opController, 5)

            binding.boundChannel shouldBe 5
            binding.boundController shouldBe opController
        }
    }

    "ActionBinding.startBinding" should {
        "set the bind button to true" {
            val binding = ActionBindingTestImpl(ButtonActions.SHOOT, oi, container)
            val bindEntry = containerNetworkTable
                .getSubTable(ButtonActions.SHOOT.label)
                .getEntry("Bind")

            binding.startBinding()
            bindEntry.value.boolean shouldBe true
        }
    }

    "ActionBinding.stopBinding" should {
        "set the bind button to false" {
            val binding = ActionBindingTestImpl(ButtonActions.SHOOT, oi, container)
            val bindEntry = containerNetworkTable
                .getSubTable(ButtonActions.SHOOT.label)
                .getEntry("Bind")

            // Set it to true so that we know that .stopBinding() changed it
            bindEntry.setBoolean(true)

            binding.stopBinding()
            bindEntry.value.boolean shouldBe false
        }
    }

    "ActionBinding.toString" should {
        "pretty-print the binding" {
            val binding = ActionBindingTestImpl(ButtonActions.SHOOT, oi, container)

            "$binding" shouldBe "XboxController 0: 1"
        }
    }
})
