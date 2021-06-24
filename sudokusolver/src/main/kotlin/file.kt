package com.knowledgespike.sudokusolver

import java.io.File


fun readAsCsv(file: File): List<CellState> {
    val data = mutableListOf<CellState>()
    val reader = file.bufferedReader()
    reader.use {
        for (row in 0..8) {
            val line = reader.readLine()
            line?.let {
                val values = it.split(",", " ", "\t").map { value ->
                    if(value == "0") "." else value
                }
                if (values.size != 9) throw Exception("Invalid CSV")
                for (column in 0..8) {
                    data.add(CellState(values[column], isEditing = false, isPreset = values[column] != "."))
                }
            }
        }
    }
    return data
}
