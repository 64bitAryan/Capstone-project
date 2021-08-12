package com.project.findme.data.entity

data class Credential(
    val uid: String = "",
    val name: String = "",
    val profession: String = "",
    val dob: String = "",
    val gender: String = "",
    val interest: List<String> = listOf(),
)