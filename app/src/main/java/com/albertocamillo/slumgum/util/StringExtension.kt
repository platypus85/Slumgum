package com.albertocamillo.slumgum.util

object StringExtension{
    fun String.capitalizeWords(): String = split(" ").asSequence().map { it.capitalize() }.joinToString(" ")
}