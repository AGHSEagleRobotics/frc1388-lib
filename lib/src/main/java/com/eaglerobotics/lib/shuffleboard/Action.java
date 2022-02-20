// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.eaglerobotics.lib.shuffleboard;

public interface Action {
    String getLabel();
    String getDescription();

    Integer getDefaultChannel();
    Integer getDefaultPort();
}
