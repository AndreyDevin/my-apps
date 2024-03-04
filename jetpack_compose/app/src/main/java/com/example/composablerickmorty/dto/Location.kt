package com.example.composablerickmorty.dto

import com.example.composablerickmorty.dto.entity.Item
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: Int,
    val name: String,
    val url: String,
    val type: String,
    val dimension: String,
    val residents: List<String>
): Item