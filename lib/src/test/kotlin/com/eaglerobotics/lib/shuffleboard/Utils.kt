package com.eaglerobotics.lib.shuffleboard

enum class AxisActions(
    private val _label: String,
    private val _description: String?,
    private val _defaultChannel: Int,
    private val _defaultPort: Int
) : InputAction {
    LEFT_DRIVE("Left Drive", "Power for left side of differential drive", 1, 0),
    RIGHT_DRIVE("Right Drive", "Power for right side of differential drive", 2, 0),
    ARM_ARTICULATE("Move Arm", "Speed of arm articulation", 4, 1);

    override fun getLabel(): String = _label

    override fun getDescription(): String? = _description

    override fun getDefaultChannel(): Int = _defaultChannel

    override fun getDefaultPort(): Int = _defaultPort
}

enum class ButtonActions(
    private val _label: String,
    private val _description: String?,
    private val _defaultChannel: Int,
    private val _defaultPort: Int
) : InputAction {
    SHOOT("Shoot", "Fire the Shooter", 1, 0),
    INTAKE_UP("Intake Up", "Raise and disable the intake", 1, 1),
    INTAKE_DOWN("Intake Down", "Lower and enable the intake", 2, 1);

    override fun getLabel(): String = _label

    override fun getDescription(): String? = _description

    override fun getDefaultChannel(): Int = _defaultChannel

    override fun getDefaultPort(): Int = _defaultPort
}
