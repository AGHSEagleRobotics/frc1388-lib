# FRC1388-lib 
_Name open for debate_

FRC1388-lib is a suite of tools developed by [FRC Team 1388 Eagle Robotics](https://eaglerobotics.com)

## Installation

FRC1388-lib is hosted in the [GitHub Maven Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry).
Unfortunately the GitHub Maven Registry requires authentication, even for public packages.
See [this recommendation by GitHub staff](https://github.community/t/download-from-github-package-registry-without-authentication/14407/111)
for instructions on how to create a token for use with the package registry.

Once you have a token, add the GitHub Maven Registry as a plugin repositories in your `settings.gradle` file:
```groovy
pluginManagement {
    repositories {
        /* ... */
        maven {
            url = uri("https://maven.pkg.github.com/AGHSEagleRobotics/frc1388-lib")
            credentials {
                username = "PublicToken"
                password = "YOUR ENCODED TOKEN"
            }
        }
    }
}
```

Then add the gradle plugin and use it to configure the extra dependencies (similar to the GradleRIO plugin:
```groovy
plugins {
    id "com.eaglerobotics.gradle-plugin" version "2022.1.1"
}

frc1388.ghpMaven(repositories)

dependencies {
    implementation frc1388.deps()
}
```

### WPILib

FRC1388-lib is built against WPILib 2022.3.1. While users may use any version of WPILib,
it is likely that some features might not work properly if using any other version of WPILib.

### SLF4J

This library leverages [SLF4J](https://slf4j.org) for logging support. While not required, it is recommended that
you include an SLF4J implementation in your robot code package. If no SLF4J implementation is
included, you will not be able to view the log statements from this library.

# Features

- [BuildInfo Gradle plugin](#buildinfo-plugin)
- [Shuffleboard Input Bindings](#shuffleboard-input-bindings)

## BuildInfo Plugin

The BuildInfo plugin creates a class called `frc.robot.BuildInfo` that contains some
metadata about the build and a function to log it via SLF4J.

### Usage

You may reference any of the stored metadata in the `BuildInfo` class directly, or simply call `.logBuildInfo()`.
Example:
```java
public void robotInit() {
    BuildInfo.logBuildInfo();
}
```

You can customize the package and class name by adding the following to your `build.gradle`:
```groovy
generateBuildInfo {
    className = "MyBuildInfo"
    classPackage = "custom.myPackage"
}
```

## Shuffleboard Input Bindings

The `ControllerBindings` class provides tools to display input bindings
on ShuffleBoard and even allow re-binding of inputs directly from shuffleboard.

### Setup

First create two enums that implement the `Action` interface, one for inputs bound to
a joystick axis and one for inputs bound to joystick buttons.

<details>
    <summary>Example</summary>

```java
public enum AxisAction implements InputAction {

    LEFT_DRIVE("Left Drive", null, 0, XboxController.Axis.kLeftY.value),
    RIGHT_DRIVE("Right Drive", null, 0, XboxController.Axis.kRightY.value);

    private final String m_name;
    private final String m_description;
    private final Integer m_defaultAxis;
    private final Integer m_defaultPort;

    AxisAction(String name, String description, Integer defaultPort, Integer defaultAxis) {
        m_name = name;
        m_description = description;
        m_defaultAxis = defaultAxis;
        m_defaultPort = defaultPort;
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public String getDescription() {
        return m_description;
    }

    @Override
    public Integer getDefaultChannel() {
        return m_defaultAxis;
    }

    @Override
    public Integer getDefaultPort() {
        return m_defaultPort;
    }
}
```
</details>

Then create an instance of `ControllerBindings`, providing your enum classes and all `GenericHID`s
you plan to use for your robot.

<details>
    <summary>Example</summary>

```java
new InputActionBindings<>(AxisActions.class, ButtonActions.class, new XboxController(0));
```
</details>

### Usage

Once you have an instance of `InputAction`, you can get the current value
of a binding by calling `getAxisValue()` or `getButtonValue()`.
Alternatively you can call `getAxisBinding()` or `getButtonBinding()` to get the `ActionBinding`
for that input. You can then call `get` on the `InputBinding` to get the current value of the bound input.

Commands can be bound to button inputs using the normal `Trigger`/`Button` API by calling 
`bindings.getButton()`.

<details>
<summary>Example</summary>

```java
bindings.getButton(ButtonInput.FIRE)
    .whenPressed(() -> System.out.println("Fire the Shooter!"));
```
</details>

Bindings may be changed programmatically by calling `InputBinding.bindTo()`.
