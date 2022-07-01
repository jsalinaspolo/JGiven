package com.tngtech.jgiven.kotest.stages

import com.tngtech.jgiven.Stage

class When : Stage<When>() {

    fun some_action(): When {
        return this
    }
}
