package com.knowledgespike.sudokusolver

import kotlinx.coroutines.flow.collect

data class CellState(
    var value: String,
    val isEditing: Boolean,
    val isPreset: Boolean
) {
    val isValid: Boolean
        get() {
            val intValue = value.toIntOrNull()
            return !(intValue == null || intValue <= 0 || intValue > 9)
        }
}
