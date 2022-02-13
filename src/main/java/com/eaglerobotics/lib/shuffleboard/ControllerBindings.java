package com.eaglerobotics.lib.shuffleboard;

import java.util.HashMap;
import java.util.Map;

import com.eaglerobotics.lib.shuffleboard.internal.OISubsystem;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.button.Button;

/**
 * Top-level class to interact with the ShuffleBoard controlelr bindings
 *
 * Simply call {@link ControllerBindings#getAxisValue(TAxis)} to get the
 * current value of the bound axis
 */
public class ControllerBindings<TAxis extends Action, TButton extends Action> {

  private final Map<TAxis, AxisBinding<TAxis>> m_axisBindings = new HashMap<>();
  private final Map<TButton, ButtonBinding<TButton>> m_buttonBindings = new HashMap<>();

  public ControllerBindings(Class<TAxis> axisEnumType, Class<TButton> buttonEnumType, GenericHID... joysticks) {
    var oi = new OISubsystem(joysticks);

    ShuffleboardTab tab = Shuffleboard.getTab("Controls");

    if (axisEnumType != null) {
      var axisTypes = axisEnumType.getEnumConstants();
      var axisBindingList = tab.getLayout("Axis Bindings", BuiltInLayouts.kList)
          .withSize(3, axisTypes.length)
          .withProperties(Map.of("Label Position", "TOP"))
          .withPosition(0, 0);
      for (TAxis axis : axisTypes) {
        m_axisBindings.put(axis, new AxisBinding<>(axis, oi, axisBindingList));
      }
    }

    if (buttonEnumType != null) {
      var buttonTypes = buttonEnumType.getEnumConstants();
      var buttonBindingList = tab.getLayout("Button Bindings", BuiltInLayouts.kList)
              .withSize(3, buttonTypes.length)
              .withProperties(Map.of("Label Position", "TOP"))
              .withPosition(3, 0);
      for (var button : buttonTypes) {
        m_buttonBindings.put(button, new ButtonBinding<>(button, oi, buttonBindingList));
      }
    }
  }

  public AxisBinding<TAxis> getAxisBinding(TAxis axis) {
    return m_axisBindings.get(axis);
  }

  public double getAxisValue(TAxis axis) {
    return m_axisBindings.get(axis).get();
  }

  public ButtonBinding<TButton> getButtonBinding(TButton button) {
    return m_buttonBindings.get(button);
  }

  public Button getButton(TButton button) {
    return new Button(m_buttonBindings.get(button));
  }
}
