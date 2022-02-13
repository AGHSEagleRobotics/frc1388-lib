package com.eaglerobotics.lib.shuffleboard.internal

import com.eaglerobotics.lib.shuffleboard.Action
import com.eaglerobotics.lib.shuffleboard.Logging
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer
import edu.wpi.first.wpilibj2.command.button.NetworkButton

/**
 * This class represents the binding between a specific {@link Action} and a given
 * channel on a particular [GenericHID]
 *
 * @param TAction The subtype of [Action]
 * @param TValue The type of the value produced by this binding
 */
internal sealed class ActionBinding<TAction : Action, TValue: Any>(
    val action: TAction,
    oi: OISubsystem,
    container: ShuffleboardContainer,
) : Logging {

    var boundController: GenericHID = oi.joysticks.find { it.port == action.defaultPort } ?: oi.joysticks[0]
        private set

    var boundChannel: Int = action.defaultChannel
        private set

    val actionName by action::name

    init {
        val grid = container.getLayout(action.name, BuiltInLayouts.kGrid)
            .withSize(2, 1)
            .withProperties(
                mapOf(
                    "Number of Columns" to 2,
                    "Number of Rows" to 1,
                    "Label position" to "HIDDEN",
                )
            )

        val bindButton = grid.add("Bind", false)
            .withWidget(BuiltInWidgets.kToggleButton)

        grid.addString("label", ::toString)

        NetworkButton(bindButton.entry)
            .whileHeld(BindAction(this, oi) { bindButton.entry.setBoolean(false) })
    }

    /**
     * Get a human readable name for the specified channel (ie an axis or button name).
     * Does NOT include the controller type or port
     *
     * @return Name of the specified channel
     */
    abstract fun getChannelName(channel: Int): String

    /**
     * Get the channel (ie axis/button number) currently being selected by the user.
     * If multiple channels are being selected, one may be chosen arbitrarily.
     *
     * @return The selected channel number from the joystick or null if none selected
     */
    abstract fun getSelectedChannel(joystick: GenericHID): Int?

    abstract operator fun invoke(): TValue

    /**
     * Bind this action to a new channel
     * @param joystick The joystick to bind to
     * @param channelNum The channel number to bind to
     */
    fun bindTo(joystick: GenericHID, channelNum: Int) {
        boundController = joystick
        boundChannel = channelNum

        log.info("Successfully bound {} to {}", action::name, this::toString)
    }

    /**
     * Convert current binding to an easy to read format
     *
     * @return A human readable string representation of the current binding value
     */
    override fun toString(): String =
        "${boundController.javaClass.simpleName} ${boundController.port}: ${getChannelName(boundChannel)}"
}
