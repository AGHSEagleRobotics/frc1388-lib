package com.eaglerobotics.lib.shuffleboard

import com.eaglerobotics.lib.shuffleboard.internal.OISubsystem
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.PS4Controller
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.simulation.XboxControllerSim
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.enum
import io.kotest.property.exhaustive.exhaustive

class AxisBindingTest : ShuffleboardWordSpec({
    // These are lazy so that they don't get created until after HAL.initialize has been called
    val xboxController by lazy { XboxController(0) }
    val xboxControllerSim by lazy { XboxControllerSim(0) }

    val ps4Controller by lazy { PS4Controller(1) }

    val oi by lazy { OISubsystem(xboxController, ps4Controller) }

    beforeTest {
        XboxController.Axis.values().forEach {
            xboxControllerSim.setRawAxis(it.value, 0.0)
        }
        xboxControllerSim.notifyNewData()
    }

    "AxisBinding.get" should {
        "return the value of the bound axis" {
            val binding = AxisBinding(AxisActions.LEFT_DRIVE, oi, container)

            xboxControllerSim.setRawAxis(AxisActions.LEFT_DRIVE.defaultChannel, 0.5)
            xboxControllerSim.notifyNewData()

            binding.get() shouldBeExactly 0.5
        }
    }

    "AxisBinding.getBoundChannelName" should {
        "return the name of XboxController axes" {
            val binding = AxisBinding(AxisActions.LEFT_DRIVE, oi, container)

            checkAll<XboxController.Axis>(Exhaustive.enum()) { axis ->
                binding.bindTo(xboxController, axis.value)
                binding.boundChannelName shouldBe "$axis"
            }
        }

        "return the name of PS4Controller axes" {
            val binding = AxisBinding(AxisActions.LEFT_DRIVE, oi, container)

            checkAll<PS4Controller.Axis>(Exhaustive.enum()) { axis ->
                binding.bindTo(ps4Controller, axis.value)
                binding.boundChannelName shouldBe "$axis"
            }
        }

        "return the channel number if not a known channel" {
            val binding = AxisBinding(AxisActions.LEFT_DRIVE, oi, container)

            checkAll(
                30,
                Arb.int(20),
                listOf(xboxController, ps4Controller, GenericHID((2))).exhaustive()
            ) { channel, controller ->
                binding.bindTo(controller, channel)

                binding.boundChannelName shouldBe "$channel"
            }
        }
    }

    "AxisBinding.getSelectedChannel" should {
        "return the id of the highest value axis" {
            val binding = AxisBinding(AxisActions.LEFT_DRIVE, oi, container)

            with(xboxControllerSim) {
                setRawAxis(1, 0.5)
                setRawAxis(2, -1.0)
                setRawAxis(3, 0.2)
                notifyNewData()
            }

            binding.getSelectedChannel(xboxController) shouldBe 2
        }

        "return null if no axis is above the deadband" {
            val binding = AxisBinding(AxisActions.LEFT_DRIVE, oi, container)

            with(xboxControllerSim) {
                setRawAxis(1, -0.2)
                setRawAxis(2, 0.0)
                setRawAxis(3, 0.2)
                notifyNewData()
            }

            binding.getSelectedChannel(xboxController) shouldBe null
        }
    }
})
