package com.knowledgespike.gameoflife

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow


data class Cell(val x: Int, val y: Int, var on: Boolean)

class Grid(val width: Dimension, val height: Dimension) {

    var cells: Array<Array<Cell>>

    init {
        cells = initCells()
    }

    private fun initCells(): Array<Array<Cell>> {
        // 6 and 35 to discount the 'chrome' on the window
        val boxesAcross = width
        val boxesDown = height

        return Array(boxesAcross.value) { w ->
            Array(boxesDown.value) { h ->
                Cell(w, h, false)
            }
        }
    }

    private fun tick(): Array<Array<Cell>> {
        // work on the clone
        val newGrid = this.deepCopyGrid()

        for (row in 0 until cells.size) {
            for (column in 0 until newGrid.cells[row].size) {
                val cell = newGrid.cells[row][column]
                val neighbours: Array<Cell> = getNeighbours(width, height, cell)
                val countOfAlive = neighbours.filter { it.on }.size
                if (cell.on) {
                    if (countOfAlive < 2) cell.on = false
                    if (countOfAlive > 3) cell.on = false
                } else {
                    if (countOfAlive == 3) cell.on = true
                }
            }
        }

        return newGrid.cells
    }

    private fun getNeighbours(width: Dimension, height: Dimension, cell: Cell): Array<Cell> {
        val neighbours = mutableListOf<Cell>()
        val widthValue = width.value
        val heightValue = height.value
        val xm1 = if (cell.x - 1 < 0) widthValue - 1 else cell.x - 1
        val xp1 = if (cell.x + 1 >= widthValue) 0 else cell.x + 1
        val ym1 = if (cell.y - 1 < 0) heightValue - 1 else cell.y - 1
        val yp1 = if (cell.y + 1 >= heightValue) 0 else cell.y + 1
        neighbours.add(cells[xm1][ym1])
        neighbours.add(cells[cell.x][ym1])
        neighbours.add(cells[xp1][ym1])
        neighbours.add(cells[xp1][cell.y])
        neighbours.add(cells[xp1][yp1])
        neighbours.add(cells[cell.x][yp1])
        neighbours.add(cells[xm1][yp1])
        neighbours.add(cells[xm1][cell.y])

        return neighbours.toTypedArray()
    }

    suspend fun start() = flow {

        while (cells.any { it.any { it.on == true } }) {
            val newCells = tick()
            emit(newCells)
            delay(100)
        }
    }
}

@JvmInline
value class Dimension(private val dim: Int) {
    val value: Int
        get() = dim
}