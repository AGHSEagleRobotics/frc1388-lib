package com.eaglerobotics.lib.shuffleboard.internal

import com.eaglerobotics.lib.shuffleboard.Action
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.PS4Controller
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

internal class AxisBinding<T : Action>(
    action: T,
    oi: OISubsystem,
    container: ShuffleboardContainer
) : ActionBinding<T, Double>(action, oi, container)  {

    override operator fun invoke() = boundController.getRawAxis(boundChannel)

    override fun getChannelName(channel: Int): String = when (boundController) {
        is XboxController -> XboxController.Axis.values()
            .firstOrNull { it.value == channel }
            ?.toString()
        is PS4Controller -> PS4Controller.Axis.values()
            .firstOrNull { it.value == channel }
            ?.toString()
        else -> null
    } ?: "$channel"

    override fun getSelectedChannel(joystick: GenericHID): Int? = sequence {
        repeat(joystick.axisCount) {
            yield(joystick.getRawAxis(it) to it)
        }
    }.maxByOrNull { it.first }?.second
}