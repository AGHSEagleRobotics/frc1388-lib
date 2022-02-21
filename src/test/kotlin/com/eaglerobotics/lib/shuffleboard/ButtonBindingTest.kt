package com.eaglerobotics.lib.shuffleboard

import com.eaglerobotics.lib.shuffleboard.internal.OISubsystem
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.PS4Controller
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.simulation.XboxControllerSim
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.enum
import io.kotest.property.exhaustive.exhaustive

class ButtonBindingTest : ShuffleboardWordSpec({
    // These are lazy so that they don't get created until after HAL.initialize has been called
    val xboxController by lazy { XboxController(0) }
    val xboxControllerSim by lazy { XboxControllerSim(0) }

    val ps4Controller by lazy { PS4Controller(1) }

    val oi by lazy { OISubsystem(xboxController, ps4Controller) }

    beforeTest {
        XboxController.Button.values().forEach {
            xboxControllerSim.setRawButton(it.value, false)
        }
        xboxControllerSim.notifyNewData()
    }

    "ButtonBinding.get" should {
        "return the value of the bound axis" {
            val binding = ButtonBinding(ButtonActions.SHOOT, oi, container)

            xboxControllerSim.setRawButton(ButtonActions.SHOOT.defaultChannel, true)
            xboxControllerSim.notifyNewData()

            binding.get() shouldBe true
        }
    }

    "ButtonBinding.getBoundChannelName" should {
        "return the name of XboxController axes" {
            val binding = ButtonBinding(ButtonActions.SHOOT, oi, container)

            checkAll<XboxController.Button>(Exhaustive.enum()) { axis ->
                binding.bindTo(xboxController, axis.value)
                binding.boundChannelName shouldBe "$axis"
            }
        }

        "return the name of PS4Controller axes" {
            val binding = ButtonBinding(ButtonActions.SHOOT, oi, container)

            checkAll<PS4Controller.Button>(Exhaustive.enum()) { axis ->
                binding.bindTo(ps4Controller, axis.value)
                binding.boundChannelName shouldBe "$axis"
            }
        }

        "return the channel number if not a known channel" {
            val binding = ButtonBinding(ButtonActions.SHOOT, oi, container)

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

    "ButtonBinding.getSelectedChannel" should {
        "return the highest id pressed button" {
            val binding = ButtonBinding(ButtonActions.SHOOT, oi, container)

            with(xboxControllerSim) {
                setRawButton(1, true)
                setRawButton(2, false)
                setRawButton(4, true)
                notifyNewData()
            }

            binding.getSelectedChannel(xboxController) shouldBe 4
        }

        "return null if no buttons are pressed" {
            val binding = ButtonBinding(ButtonActions.SHOOT, oi, container)

            with(xboxControllerSim) {
                setRawButton(1, false)
                setRawButton(2, false)
                setRawButton(4, false)
                notifyNewData()
            }

            binding.getSelectedChannel(xboxController) shouldBe null
        }
    }
})
