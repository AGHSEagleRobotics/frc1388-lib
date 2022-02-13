package com.eaglerobotics.lib.shuffleboard

interface Action {
    val name: String
    val description: String?

    val defaultChannel: Int
    val defaultPort: Int
}
