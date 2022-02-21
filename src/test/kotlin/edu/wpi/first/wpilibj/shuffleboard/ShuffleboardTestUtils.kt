package edu.wpi.first.wpilibj.shuffleboard

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <C : ShuffleboardComponent<C>> containProperty(property: Pair<String, Any>) = object : Matcher<C> {
    override fun test(value: C) = MatcherResult(
        value.properties[property.first] == property.second,
        { "ShuffleboardComponent ${value.title} should have property $property" },
        { "ShuffleboardComponent ${value.title} should not have property $property" }
    )
}

infix fun <C : ShuffleboardComponent<C>> C.shouldContainProperty(property: Pair<String, Any>) = this should containProperty(property)
infix fun <C : ShuffleboardComponent<C>> C.shouldNotContainProperty(property: Pair<String, Any>) = this shouldNot containProperty(property)

fun <C : ShuffleboardComponent<C>> containAllProperties(properties: Map<String, Any>) = object : Matcher<C> {
    override fun test(value: C) = MatcherResult(
        properties.all { value.properties[it.key] == it.value },
        { "ShuffleboardComponent ${value.title} should have properties: $properties" },
        { "ShuffleboardComponent ${value.title} should not have properties: $properties" }
    )
}

infix fun <C : ShuffleboardComponent<C>> C.shouldContainAllProperties(properties: Map<String, Any>) = this should containAllProperties(properties)
infix fun <C : ShuffleboardComponent<C>> C.shouldNotContainAllProperties(properties: Map<String, Any>) = this shouldNot containAllProperties(properties)
