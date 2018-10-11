package com.albertocamillo.slumgum.manager

class DatabaseManager{

    enum class Table(var tableName: String) {
        USERS("users"),
        SWARMS("swarms"),
        USER_SWARMS("user-swarms")
    }
}