package com.eaglerobotics.lib.shuffleboard.internal

import com.eaglerobotics.lib.shuffleboard.Logging
import edu.wpi.first.wpilibj2.command.CommandBase

internal class BindAction(
    private val binding: ActionBinding<*,*>,
    private val oi: OISubsystem,
    private val finalize: () -> Unit,
) : CommandBase(), Logging {

    private var done = false

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(oi)
    }

    override fun initialize() {
        log.debug("Rebinding {}. Waiting for input...", )
        done = false
    }

    override fun execute() {
        oi.joysticks.forEach {
            val selectedChannel = binding.getSelectedChannel(it)
            if (selectedChannel != null) {
                binding.bindTo(it, selectedChannel)

                done = true
                return
            }
        }
    }

    override fun isFinished(): Boolean = done

    override fun end(interrupted: Boolean) {
        finalize()
    }

    override fun runsWhenDisabled(): Boolean = true
}
