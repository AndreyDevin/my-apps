package com.example.composablerickmorty.dto.entity

import com.example.composablerickmorty.dto.Info

interface Response {
    val info: Info
    val results: List<Item>
}