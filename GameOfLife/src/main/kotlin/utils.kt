package com.knowledgespike.gameoflife


fun drawGlider(grid: Grid, initialCellX: Int, initialCellY: Int) {

    val x = if (initialCellX > grid.width.value - 4) grid.width.value - 4 else initialCellX
    val y = if (initialCellY < 2) 2 else initialCellY

    grid.cells[x + 1][y - 2] = Cell(x + 1, y - 2, true)
    grid.cells[x + 2][y - 1] = Cell(x + 2, y - 1, true)
    grid.cells[x][y] = Cell(x, y, true)
    grid.cells[x + 1][y] = Cell(x + 1, y, true)
    grid.cells[x + 2][y] = Cell(x + 2, y, true)
}

fun drawGosper(grid: Grid) {

    val x = 10
    val y = 10

    grid.cells[x][y] = Cell(x, y, true)
    grid.cells[x][y + 1] = Cell(x, y + 1, true)
    grid.cells[x+1][y] = Cell(x+1, y, true)
    grid.cells[x+1][y+1] = Cell(x+1, y+1, true)

    grid.cells[x+10][y] = Cell(x+10, y, true)
    grid.cells[x+10][y+1] = Cell(x+10, y+1, true)
    grid.cells[x+10][y+2] = Cell(x+10, y+2, true)

    grid.cells[x+11][y-1] = Cell(x+11, y-1, true)
    grid.cells[x+12][y-2] = Cell(x+12, y-2, true)
    grid.cells[x+13][y-2] = Cell(x+13, y-2, true)

    grid.cells[x+11][y+3] = Cell(x+11, y+3, true)
    grid.cells[x+12][y+4] = Cell(x+12, y+4, true)
    grid.cells[x+13][y+4] = Cell(x+13, y+4, true)

    grid.cells[x+14][y+1] = Cell(x+14, y+1, true)

    grid.cells[x+15][y-1] = Cell(x+15, y-1, true)
    grid.cells[x+16][y] = Cell(x+16, y, true)
    grid.cells[x+16][y+1] = Cell(x+16, y+1, true)
    grid.cells[x+16][y+2] = Cell(x+16, y+2, true)
    grid.cells[x+15][y+3] = Cell(x+15, y+3, true)
    grid.cells[x+17][y+1] = Cell(x+17, y+1, true)

    grid.cells[x+20][y] = Cell(x+20, y, true)
    grid.cells[x+21][y] = Cell(x+21, y, true)
    grid.cells[x+20][y-1] = Cell(x+20, y-1, true)
    grid.cells[x+21][y-1] = Cell(x+21, y-1, true)
    grid.cells[x+20][y-2] = Cell(x+20, y-2, true)
    grid.cells[x+21][y-2] = Cell(x+21, y-2, true)

    grid.cells[x+22][y-3] = Cell(x+22, y-3, true)
    grid.cells[x+24][y-3] = Cell(x+24, y-3, true)
    grid.cells[x+24][y-4] = Cell(x+24, y-4, true)
    grid.cells[x+22][y+1] = Cell(x+22, y+1, true)
    grid.cells[x+24][y+1] = Cell(x+24, y+1, true)
    grid.cells[x+24][y+2] = Cell(x+24, y+2, true)

    grid.cells[x+34][y-1] = Cell(x+34, y-1, true)
    grid.cells[x+34][y-2] = Cell(x+34, y-2, true)
    grid.cells[x+35][y-1] = Cell(x+35, y-1, true)
    grid.cells[x+35][y-2] = Cell(x+35, y-2, true)
}

fun Grid.shallowCopyGrid(
): Grid {
    val newGrid = Grid(this.width, this.height)
    newGrid.cells = this.cells
    return newGrid
}

fun Grid.deepCopyGrid(

): Grid {
    val newGrid = Grid(this.width, this.height)
    this.cells.forEachIndexed { rowNdx, row ->
        row.forEachIndexed { columnNdx, cell ->
            newGrid.cells[rowNdx][columnNdx] = cell.copy()
        }
    }
    return newGrid
}