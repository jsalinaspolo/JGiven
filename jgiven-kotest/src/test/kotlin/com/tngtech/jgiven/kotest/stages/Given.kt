package com.tngtech.jgiven.kotest.stages

import com.tngtech.jgiven.Stage

class Given : Stage<Given>() {

    fun some_state(): Given {
        return this
    }

    fun `some_state_$`(param: String): Given {
        return this
    }
}


