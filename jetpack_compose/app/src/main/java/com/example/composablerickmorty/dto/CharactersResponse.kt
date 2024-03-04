package com.example.composablerickmorty.dto

import com.example.composablerickmorty.dto.entity.Response
import kotlinx.serialization.Serializable

@Serializable
data class CharactersResponse(
    override val info: Info,
    override val results: List<Character>
): Response