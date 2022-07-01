package com.tngtech.jgiven.kotest

import com.tngtech.jgiven.base.ScenarioTestBase
import com.tngtech.jgiven.impl.Scenario
import io.kotest.core.spec.style.StringSpec

//class ScenarioSpec<GIVEN, WHEN, THEN> : ScenarioTestBase<GIVEN, WHEN, THEN>() {
class ScenarioSpec<GIVEN, WHEN, THEN> : StringSpec() {

//    override fun getScenario(): Scenario<GIVEN, WHEN, THEN> = createScenario()

    fun Given(): GIVEN = getScenario().given()
    fun When(): WHEN = getScenario().`when`()
    fun Then(): THEN = getScenario().then()
}