// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.eaglerobotics.lib.shuffleboard;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.eaglerobotics.lib.shuffleboard.internal.OISubsystem;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class that manages the ShuffleBoard aspects of binding a specific Axis provided
 * in the {@link InputAction} subtype to a particular joystick port and axis
 * 
 * Also includes some helper functions for retriving the current value of the bound
 * axis from the joystick
 */
public class AxisBinding<T extends InputAction> extends InputBinding<T, Double> {

  private static final Logger log = LoggerFactory.getLogger(AxisBinding.class);

  private static final double AXIS_DEADBAND = 0.5;


  public AxisBinding(T axis, OISubsystem oi, ShuffleboardContainer container) {
    super(axis, oi, container);
  }

  /**
   * Reads the value of the bound controller axis
   * 
   * @return The bound controller axis value
   */
  @Override
  public Double get() {
    return getBoundJoystick().getRawAxis(getBoundChannel());
  }

  private static final Map<Class<? extends GenericHID>, Function<Integer, String>> axisPrettifiers = Map.of(

      XboxController.class, axisNum -> Stream.of(XboxController.Axis.values())
          .filter(axis -> axis.value == axisNum)
          .findAny()
          .map(XboxController.Axis::toString)
          .orElse(axisNum.toString()),

      PS4Controller.class, axisNum -> Stream.of(PS4Controller.Axis.values())
          .filter(axis -> axis.value == axisNum)
          .findAny()
          .map(PS4Controller.Axis::toString)
          .orElse(axisNum.toString()));


  @Override
  public String getBoundChannelName() {
    var boundController = getBoundJoystick();

    var prettifier = axisPrettifiers.getOrDefault(
            boundController.getClass(),
            Object::toString);

    return prettifier.apply(getBoundChannel());
  }

  @Override
  public Integer getSelectedChannel(GenericHID joystick) {
    Integer largestAxis = null;
    double largestValue = AXIS_DEADBAND;

    var axisCount = joystick.getAxisCount();
    for (int i = 0; i < axisCount; i++) {
      var axisValue = Math.abs(joystick.getRawAxis(i));
      if (axisValue > largestValue) {
        largestValue = axisValue;
        largestAxis = i;
      }
    }
    return largestAxis;
  }
}
