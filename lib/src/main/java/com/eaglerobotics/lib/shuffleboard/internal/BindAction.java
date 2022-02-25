package com.eaglerobotics.lib.shuffleboard.internal;

import com.eaglerobotics.lib.shuffleboard.InputBinding;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj2.command.CommandBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Command that will listen for the first channel to be activated (ie button pressed/axis moved)
 * on any connected controller.
 */
public class BindAction extends CommandBase {

  private static final Logger log = LoggerFactory.getLogger(BindAction.class);

  private final OISubsystem m_oiSubsystem;

  private final InputBinding<?, ?> m_binding;

  private boolean m_done = false;

  public BindAction(InputBinding<?, ?> binding, OISubsystem oiSubsystem) {
    m_oiSubsystem = oiSubsystem;
    m_binding = binding;
    // each subsystem used by the command must be passed into the
    // addRequirements() method (which takes a vararg of Subsystem)
    addRequirements(oiSubsystem);
  }

  @Override
  public void initialize() {
    log.atDebug()
        .addArgument(m_binding::getActionName)
        .log("Rebinding {}. Waiting for input");
    m_done = false;
  }

  @Override
  public void execute() {
    for (GenericHID joystick: m_oiSubsystem.getJoysticks()) {
      var selectedChannel = m_binding.getSelectedChannel(joystick);
      if (selectedChannel != null) {
        m_binding.bindTo(joystick, selectedChannel);

        m_done = true;
        return;
      }
    }
  }

  @Override
  public boolean isFinished() {
    return m_done;
  }

  @Override
  public boolean runsWhenDisabled() {
    // Allow rebinding while robot is disabled.
    return true;
  }

  @Override
  public void end(boolean interrupted) {
    m_binding.stopBinding();
  }
}
