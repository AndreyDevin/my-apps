package com.example.composablerickmorty.dto

import com.example.composablerickmorty.dto.entity.Item
import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val origin: Location, //Место рождения
    val image: String,
    val url: String,
    val location: Location //Последнее место нахождения
): Item