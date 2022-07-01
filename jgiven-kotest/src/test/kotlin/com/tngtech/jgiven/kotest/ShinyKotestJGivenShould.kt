package com.tngtech.jgiven.kotest

import com.tngtech.jgiven.annotation.ExtendedDescription
import com.tngtech.jgiven.kotest.stages.Given
import com.tngtech.jgiven.kotest.stages.Then
import com.tngtech.jgiven.kotest.stages.When

class ShinyKotestJGivenShould : ScenarioSpec<Given, When, Then>() {

    fun "something should happen"() {
        Given().some_state()
        When().some_action()
        Then().some_outcome()

        assert scenario.scenarioModel.description == "something should happen"
    }

    fun "be some able to use params"() {
        given().some_state_$("param")
        `when`().some_action()
        then().some_outcome()

        assert scenario.scenarioModel.scenarioCases.first().getStep(0).words.join(" ") == "Given some state param"
    }

    @ExtendedDescription("more details")
    fun "be able to have extended descriptions"() {
        given().some_state()
        when().some_action()
        then().some_outcome()

        assert scenario.scenarioModel.extendedDescription == "more details"
    }

    fun "be able to use tables #param"() {
        given().some_state_$(param)
        when().some_action()
        then().some_outcome()

        assert scenario.scenarioModel.getDescription() == "be able to use tables #param"

        where:
        param        | _
        "param"      | _
        "word param" | _
    }
}
