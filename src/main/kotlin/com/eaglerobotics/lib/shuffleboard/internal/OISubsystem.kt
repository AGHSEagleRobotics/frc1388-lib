package com.eaglerobotics.lib.shuffleboard.internal

import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj2.command.SubsystemBase

class OISubsystem(vararg val joysticks: GenericHID): SubsystemBase() {
    init {
        if (joysticks.isEmpty()) {
            throw IllegalArgumentException("Must provide at least one joystick")
        }
    }
}