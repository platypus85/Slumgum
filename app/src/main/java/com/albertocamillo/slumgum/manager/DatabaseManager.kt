package com.albertocamillo.slumgum.manager

import com.albertocamillo.slumgum.data.Swarm
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class DatabaseManager{

    companion object {

        private fun getSwarmCollection(): CollectionReference{
           return FirebaseFirestore.getInstance().collection("swarms")
       }

        fun addNewSwarm(swarm: Swarm){
            getSwarmCollection().add(mapOf(
                    "latitude" to swarm.latitude,
                    "longitude" to swarm.longitude,
                    "description" to swarm.description,
                    "dateReport" to swarm.dateReport,
                    "collected" to swarm.collected
            ))
        }
    }

    enum class Table(var tableName: String) {
        USERS("users"),
        SWARMS("swarms"),
        USER_SWARMS("user-swarms")
    }
}