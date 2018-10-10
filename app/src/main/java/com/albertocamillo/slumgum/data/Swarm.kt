package com.albertocamillo.slumgum.data

import java.util.*

data class Swarm(var latitude: Long = 0,
                 var longitude: Long = 0,
                 var userId: String? = null,
                 var description: String? = null,
                 var dateReport: Date? = null,
                 var collected: Boolean = false)

