package com.tngtech.jgiven.kotest.stages

import com.tngtech.jgiven.Stage
import io.kotest.matchers.shouldBe

class Then : Stage<Then>() {

    fun some_outcome(): Then {
        shouldBe(true)
        return this
    }
}
