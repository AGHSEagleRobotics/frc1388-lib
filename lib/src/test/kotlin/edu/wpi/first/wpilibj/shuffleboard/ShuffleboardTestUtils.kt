package edu.wpi.first.wpilibj.shuffleboard

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <C : ShuffleboardComponent<C>> haveProperty(property: Pair<String, Any>) = object : Matcher<C> {
    override fun test(value: C) = MatcherResult(
        value.properties[property.first] == property.second,
        { "ShuffleboardComponent ${value.title} should have property $property" },
        { "ShuffleboardComponent ${value.title} should not have property $property" }
    )
}

infix fun <C : ShuffleboardComponent<C>> C.shouldHaveProperty(property: Pair<String, Any>) = this should haveProperty(property)
infix fun <C : ShuffleboardComponent<C>> C.shouldNotHaveProperty(property: Pair<String, Any>) = this shouldNot haveProperty(property)

fun <C : ShuffleboardComponent<C>> haveAllProperties(properties: Map<String, Any>) = object : Matcher<C> {
    override fun test(value: C) = MatcherResult(
        properties.all { value.properties[it.key] == it.value },
        { "ShuffleboardComponent ${value.title} should have properties: $properties" },
        { "ShuffleboardComponent ${value.title} should not have properties: $properties" }
    )
}

infix fun <C : ShuffleboardComponent<C>> C.shouldHaveAllProperties(properties: Map<String, Any>) = this should haveAllProperties(properties)
infix fun <C : ShuffleboardComponent<C>> C.shouldNotHaveAllProperties(properties: Map<String, Any>) = this shouldNot haveAllProperties(properties)
