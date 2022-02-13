package com.eaglerobotics.lib.shuffleboard.internal

import com.eaglerobotics.lib.shuffleboard.Action
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.PS4Controller
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer
import edu.wpi.first.wpilibj2.command.button.Button

internal class ButtonBinding<T: Action>(
    action: T,
    oi: OISubsystem,
    container: ShuffleboardContainer
) : ActionBinding<T, Boolean>(action, oi, container) {
    override fun invoke(): Boolean = boundController.getRawButton(boundChannel)

    override fun getChannelName(channel: Int): String = when (boundController) {
        is XboxController -> XboxController.Button.values()
            .firstOrNull { it.value == channel }
            ?.toString()
        is PS4Controller -> PS4Controller.Button.values()
            .firstOrNull { it.value == channel }
            ?.toString()
        else -> null
    } ?: "$channel"

    override fun getSelectedChannel(joystick: GenericHID): Int? =
        when (val buttonField = DriverStation.getStickButtons(joystick.port)) {
            0 -> null
            // Return log2 rounded down of the bit field plus one.
            // This will extract the position of the highest 1 bit in the field
            // Then we add one since button channel numbers start at 1
            else -> (Math.log(buttonField.toDouble()) / Math.log(2.0)).toInt() + 1
        }

    fun toButton(): Button = Button(::invoke)
}