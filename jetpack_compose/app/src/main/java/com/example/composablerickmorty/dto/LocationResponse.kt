package com.example.composablerickmorty.dto

import com.example.composablerickmorty.dto.entity.Response

data class LocationResponse(
    override val info: Info,
    override val results: List<Location>
): Response