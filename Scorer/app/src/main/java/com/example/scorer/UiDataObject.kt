package com.example.scorer

data class UiDataObject(
    val year: String,
    val yearTotalDuration: String,
    val weekList: List<Map<Long, Long>>,
    val monthList: Map<String, Long>
)
