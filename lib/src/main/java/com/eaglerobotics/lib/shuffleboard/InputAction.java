// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.eaglerobotics.lib.shuffleboard;

/**
 * Describes an input action the drivers can perform
 */
public interface InputAction {
    /**
     * @return The display name of the InputAction
     */
    String getLabel();

    /**
     * Currently unused
     * @return A short description of the Input
     */
    String getDescription();

    /**
     * Return the default channel (ie axis/button id) for this action.
     *
     * @return The default channel for this action
     */
    Integer getDefaultChannel();

    /**
     * @return The default USB port to bind this action to
     */
    Integer getDefaultPort();
}
