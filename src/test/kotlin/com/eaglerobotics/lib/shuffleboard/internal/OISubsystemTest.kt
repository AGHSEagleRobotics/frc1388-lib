package com.eaglerobotics.lib.shuffleboard.internal

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class OISubsystemTest : WordSpec({
    "OISubsystem constructor" should {
        "throw if not given any GenericHIDs" {
            val ex = shouldThrow<IllegalArgumentException> { OISubsystem() }

            ex.message shouldBe "Must provide at least one joystick"
        }
    }
})
