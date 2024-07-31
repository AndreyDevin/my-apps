package com.example.scorer

data class UiDataObject(
    val year: String,
    val weekList: List<Map<Long, Long>>,
    val monthList: Map<String, Long>
)
