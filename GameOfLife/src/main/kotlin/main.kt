package com.knowledgespike.gameoflife


import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import kotlin.random.Random

val BOX_SIZE = 10
val BUTTON_COLUMN_WIDTH = 200


fun main() = Window {
    var windowSize by remember { mutableStateOf(IntSize.Zero) }
    var gridDimensions by remember { mutableStateOf(Pair(Dimension(0), Dimension(0))) }
    var windowLocation by remember { mutableStateOf(IntOffset.Zero) }
    var grid: Grid? by remember { mutableStateOf(null) }
    var scope: CoroutineScope = rememberCoroutineScope()
    var currentJob by remember { mutableStateOf<Job?>(null) }

    val rnd by remember { mutableStateOf(Random(LocalDateTime.now().nano)) }



    LocalAppWindow.current.apply {
        events.onResize = {
            windowSize = it
            gridDimensions =
                Pair(Dimension((width - BUTTON_COLUMN_WIDTH - 6) / BOX_SIZE), Dimension((height - 35) / BOX_SIZE))
            grid = Grid(gridDimensions.first, gridDimensions.second)
        }
        events.onRelocate = { windowLocation = it }
    }

    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxHeight()
                    .border(width = 1.dp, color = Color.Black)
            ) {
                DrawGrid(grid) { cell ->
                    cell.on = !cell.on

                    grid?.let {
                        it.cells[cell.x][cell.y] = cell
                        grid = it.shallowCopyGrid()
                    }
                }
            }
            Column(
                modifier = Modifier.width(BUTTON_COLUMN_WIDTH.dp)
                    .padding(3.dp)
                    .fillMaxHeight()
                    .border(width = 1.dp, color = Color.Black)
            ) {

                DrawButtons(onRun = {
                    grid?.let { g ->
                        currentJob = scope.launch {
                            g.start().collect {
                                g.cells = it
                                grid = g.shallowCopyGrid()
                            }
                        }
                    }
                }, onDrawGlider = {
                    val initialCellX = rnd.nextInt(gridDimensions.first.value)
                    val initialCellY = rnd.nextInt(gridDimensions.second.value)

                    grid?.let {
                        drawGlider(it, initialCellX, initialCellY)
                    }
                    grid = grid?.shallowCopyGrid()
                }, onDrawGosper = {

                    grid?.let {
                        drawGosper(it)
                    }
                    grid = grid?.shallowCopyGrid()
                }, onStop = {
                    currentJob?.cancel()
                }, onClear = {
                    currentJob?.cancel()
                    grid = Grid(gridDimensions.first, gridDimensions.second)
                }, onRandom = {
                    grid = randomize(grid)
                })
            }
        }
    }
}

fun randomize(grid: Grid?): Grid? {
    val r = Random(LocalDateTime.now().nano)

    grid?.let {
        for (row in 0 until it.cells.size) {
            for (column in 0 until it.cells[row].size) {
                if (r.nextInt(10) == 0) {
                    it.cells[row][column].on = !(it.cells[row][column].on)
                }
            }
        }
    }
    return grid?.deepCopyGrid()
}


@Composable
fun DrawButtons(
    onRun: () -> Unit,
    onStop: () -> Unit,
    onClear: () -> Unit,
    onRandom: () -> Unit,
    onDrawGlider: () -> Unit,
    onDrawGosper: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onRandom,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("RandomizeCells")
        }

        Button(
            onClick = onDrawGlider,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Draw Glider")
        }

        Button(
            onClick = onDrawGosper,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Draw Gosper")
        }

        Button(
            onClick = onRun,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Run")
        }

        Button(
            onClick = onStop,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Stop")
        }


        Button(
            onClick = onClear,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Clear")
        }
    }
}


@Composable
fun DrawGrid(grid: Grid?, onCellClick: (cell: Cell) -> Unit) {

    grid?.let {
        // 6 and 35 to discount the 'chrome' on the window

        val width = grid.cells.size
        val height = grid.cells[0].size

        for (rows in 0..height - 1) {
            Row {
                for (columns in 0..width - 1) {
                    Column {
                        val color = if (grid.cells[columns][rows].on) Color.Black else Color.White
                        val cell by remember { mutableStateOf(grid.cells[columns][rows]) }
                        Box(
                            modifier = Modifier
                                .width(BOX_SIZE.dp)
                                .height(BOX_SIZE.dp)
                                .border(width = 1.dp, color = Color.Black)
                                .background(color = color)
                                .clickable {
                                    onCellClick(cell)
                                }
                        ) {
                        }
                    }
                }
            }
        }
    }
}
