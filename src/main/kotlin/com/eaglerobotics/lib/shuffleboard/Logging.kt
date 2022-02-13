package com.eaglerobotics.lib.shuffleboard

import org.slf4j.LoggerFactory

interface Logging {
    val log get() = LoggerFactory.getLogger(this.javaClass)
}
