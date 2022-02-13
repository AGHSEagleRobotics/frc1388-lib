package com.eaglerobotics.lib.shuffleboard

import com.eaglerobotics.lib.shuffleboard.internal.AxisBinding
import com.eaglerobotics.lib.shuffleboard.internal.ButtonBinding
import com.eaglerobotics.lib.shuffleboard.internal.OISubsystem
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj2.command.button.Button

/**
 * Top-level class to interact with the ShuffleBoard controlelr bindings
 *
 * Simply call [ControllerBindings.getAxisValue] or [ControllerBindings.getButton] to get the
 * current value of the bound action
 */
class ControllerBindings<TAxis : Action, TButton : Action>(
    axisEnumType: Class<TAxis>,
    buttonEnumType: Class<TButton>,
    vararg joysticks: GenericHID
) {
    private val axisBindings: MutableMap<TAxis, AxisBinding<TAxis>> = HashMap()
    private val buttonBindings: MutableMap<TButton, ButtonBinding<TButton>> = HashMap()

    init {
        val oi = OISubsystem(*joysticks)
        val tab = Shuffleboard.getTab("Controls")

        val axisTypes = axisEnumType.enumConstants
        val axisBindingList = tab.getLayout("Axis Bindings", BuiltInLayouts.kList)
            .withSize(3, axisTypes.size)
            .withProperties(mapOf("Label Position" to "TOP"))
            .withPosition(0, 0)
        for (axis in axisTypes) {
            axisBindings[axis] = AxisBinding(axis, oi, axisBindingList)
        }

        val buttonTypes = buttonEnumType.enumConstants
        val buttonBindingList = tab.getLayout("Button Bindings", BuiltInLayouts.kList)
            .withSize(3, buttonTypes.size)
            .withProperties(mapOf("Label Position" to "TOP"))
            .withPosition(3, 0)
        for (button in buttonTypes) {
            buttonBindings[button] = ButtonBinding(button, oi, buttonBindingList)
        }
    }

    fun getAxisValue(axis: TAxis): Double {
        return axisBindings[axis]!!.invoke()
    }

    fun getButton(button: TButton): Button {
        return buttonBindings[button]!!.toButton()
    }
}