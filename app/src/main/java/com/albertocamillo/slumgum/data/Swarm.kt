package com.albertocamillo.slumgum.data

import com.google.firebase.database.Exclude
import java.util.*

data class Swarm(var latitude: Double = 0.0,
                 var longitude: Double = 0.0,
                 var description: String,
                 var dateReport: Date,
                 var collected: Boolean = false,
                 var photoUrl: String = "") {
    constructor() : this(0.0, 0.0, "", Calendar.getInstance().time, false, "")

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["latitude"] = latitude
        result["longitude"] = longitude
        result["description"] = description
        result["dateReport"] = dateReport
        result["collected"] = collected
        result["photoUrl"] = photoUrl
        return result
    }
}

