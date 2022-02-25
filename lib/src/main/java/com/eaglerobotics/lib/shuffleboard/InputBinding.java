// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.eaglerobotics.lib.shuffleboard;

import java.util.Map;
import java.util.function.Supplier;

import com.eaglerobotics.lib.shuffleboard.internal.BindAction;
import com.eaglerobotics.lib.shuffleboard.internal.OISubsystem;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj2.command.button.NetworkButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class represents the binding between a specific {@link InputAction} and a given
 * channel on a particular {@link GenericHID}.
 * @param <TAction> The subtype of action
 */
public abstract class InputBinding<TAction extends InputAction, TValue> implements Supplier<TValue> {
  private static final Logger log = LoggerFactory.getLogger(InputBinding.class);


  protected final TAction m_action;
  protected final OISubsystem m_oi;

  private final SimpleWidget m_bindButton;

  private GenericHID m_boundJoystick;
  private int m_boundChannel;

  public InputBinding(TAction axis, OISubsystem oi, ShuffleboardContainer container) {
    m_action = axis;
    m_oi = oi;

    setupDefaultBinding();

    var grid = container.getLayout(m_action.getLabel(), BuiltInLayouts.kGrid)
        .withSize(2, 1)
        .withProperties(Map.of(
            "Number of Columns", 2,
            "Number of Rows", 1,
            "Label position", "HIDDEN"));

    m_bindButton = grid.add("Bind", false)
        .withWidget(BuiltInWidgets.kToggleButton);

    grid.addString("label", this::toString);

    new NetworkButton(m_bindButton.getEntry()).whileHeld(new BindAction(this, m_oi));

    log.atDebug()
        .addArgument(axis::getLabel)
        .log("ShuffleBoard configured for action {}");
  }

  /**
   * Get a human-readable name for the bound channel (ie an axis or button name).
   * Does NOT include the controller type or port
   * @return Name of the specified channel
   */
  public abstract String getBoundChannelName();

  /**
   * Get the channel (ie axis/button number) currently being selected by the user.
   * If multiple channels are being selected, one may be chosen arbitrarily.
   * @return The selected channel number from the joystick or null if none selected
   */
  public abstract Integer getSelectedChannel(GenericHID joystick);

  /**
   * @return The axis being bound
   */
  public TAction getAction() {
    return m_action;
  }

  /**
   * @return The name of the axis being bound
   */
  public String getActionName() {
    return m_action.getLabel();
  }

  /**
   * @return The {@link GenericHID} this action is currently bound to
   */
  public GenericHID getBoundJoystick() {
    return m_boundJoystick;
  }

  /**
   * @return The channel on the {@link this#getBoundJoystick()}
   */
  public int getBoundChannel() {
    return m_boundChannel;
  }

  /**
   * Bind this action to a new channel
   * @param joystick The joystick to bind to
   * @param channelNum The channel number to bind to
   */
  public void bindTo(GenericHID joystick, int channelNum) {
    m_boundJoystick = joystick;
    m_boundChannel = channelNum;

    log.atInfo()
        .addArgument(m_action::getLabel)
        .addArgument(this::toString)
        .log("Successfully bound {} to {}");
  }

  /**
   * Programmatically start trying to bind this action to a new channel.
   * This is the same as clicking the appropriate "Bind" button on shuffleboard
   *
   * Does nothing if already binding this action
   */
  public void startBinding() {
    m_bindButton.getEntry().setBoolean(true);
  }

  /**
   * Programmatically stops trying to bind this action to a new channel.
   * This is the same as clicking the appropriate "Bind" button on shuffleboard
   *
   * Does nothing if not currently trying to bind
   */
  public void stopBinding() {
    m_bindButton.getEntry().setBoolean(false);
  }

  private void setupDefaultBinding() {
    if (m_boundJoystick == null) {
      var joysticks = m_oi.getJoysticks();
      var defaultJoystick = joysticks.get(0);
      for (GenericHID joystick : joysticks) {
        if (joystick.getPort() == m_action.getDefaultPort()) {
          defaultJoystick = joystick;
          break;
        }
      }

      bindTo(defaultJoystick, m_action.getDefaultChannel());
    }
  }

  /**
   * Convert current binding to an easy to read format
   *
   * @return A human readable string representation of the current binding value
   */
  @Override
  public String toString() {
    return String.format(
            "%s %d: %s",
            m_boundJoystick.getClass().getSimpleName(),
            m_boundJoystick.getPort(),
            getBoundChannelName()
    );
  }
}
