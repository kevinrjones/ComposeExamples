package com.knowledgespike.sudokusolver


fun to2DArray(cells: List<CellState>): Array<CharArray> {

    val cellArray = Array<CharArray>(9, { CharArray(9) })
    for (rowNdx in 0..8) {
        for (columnNdx in 0..8) {
            cellArray[rowNdx][columnNdx] = cells[(rowNdx * 9) + columnNdx].value.toCharArray().first()
            if (cellArray[rowNdx][columnNdx] == '0') cellArray[rowNdx][columnNdx] = '.'
        }
    }

    return cellArray
}

fun initializeCells(cells: MutableList<CellState>) {
    println("Initialize cells")
    for (ndx in 0..80) {
        cells.add(CellState(".", isEditing = false, isPreset = true))
    }
}


fun initializeIsEditing(editing: MutableList<Boolean>) {
    for (ndx in 0..80)
        editing.add(false)
}