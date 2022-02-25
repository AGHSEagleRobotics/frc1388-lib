package com.eaglerobotics.lib.shuffleboard.internal

import com.eaglerobotics.lib.shuffleboard.InputAction
import com.eaglerobotics.lib.shuffleboard.InputBinding
import com.eaglerobotics.lib.shuffleboard.ShuffleboardWordSpec
import edu.wpi.first.wpilibj.PS4Controller
import edu.wpi.first.wpilibj.XboxController
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.ints
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class BindActionTest : ShuffleboardWordSpec({
    val binding: InputBinding<InputAction, Any?> = mockk(relaxed = true)

    // These are lazy so that they don't get created until after HAL.initialize has been called
    val xboxController by lazy { XboxController(0) }

    val ps4Controller by lazy { PS4Controller(1) }

    val oi by lazy { OISubsystem(xboxController, ps4Controller) }

    val command by lazy { BindAction(binding, oi) }

    beforeTest {
        clearAllMocks()
    }

    "BindAction" should {
        "require the oi subsystem" {
            command.requirements shouldContain oi
        }

        "run when disabled" {
            command.runsWhenDisabled() shouldBe true
        }

        "bind to largest axis" {
            checkAll(Exhaustive.ints(0..1), Exhaustive.ints(1..6)) { port, channel ->
                every { binding.getSelectedChannel(any()) } returns null
                every { binding.getSelectedChannel(oi.joysticks[port]) } returns channel

                command.initialize()
                command.execute()

                command.isFinished shouldBe true
                verify { binding.bindTo(oi.joysticks[port], channel) }
                verify { binding.getSelectedChannel(any()) }
                confirmVerified(binding)
            }
        }

        "not bind before an axis is selected" {
            every { binding.getSelectedChannel(any()) } returns null

            command.initialize()

            repeat(10) {
                command.execute()
                command.isFinished shouldBe false
            }
        }

        "call .stopBinding() when command ends" {
            checkAll(Exhaustive.boolean()) {
                command.end(it)

                verify { binding.stopBinding() }
                confirmVerified()
            }
        }
    }
})
