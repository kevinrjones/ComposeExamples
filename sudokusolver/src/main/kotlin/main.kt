package com.knowledgespike.sudokusolver

import androidx.compose.desktop.ComposeWindow
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.io.File
import java.io.File.separator


fun main() = Window(
    title = "Class File Viewer",
    size = IntSize(860, 620),
    resizable = false
) {
    val cells = remember { mutableStateListOf<CellState>() }

    val origninalCells = remember { mutableStateListOf<CellState>() }
    var openFileAction by mutableStateOf(false)
    var file: File? by remember { mutableStateOf(null) }

    val scope: CoroutineScope = rememberCoroutineScope()
    initializeCells(cells)
    initializeCells(origninalCells)

    MaterialTheme {
        if (openFileAction) {
            val files = openFileDialog(LocalAppWindow.current.window, "Open File", listOf(".csv"))
            file = if (files.isEmpty()) null else files.first()
            openFileAction = false
            val loadedCells = readAsCsv(file!!)
            loadedCells.forEachIndexed { ndx, value ->
                cells[ndx] = value.copy()
            }
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxHeight()
                    .weight(0.66f)
                    .border(width = 1.dp, color = Color.Black)
            ) {
                DrawGrid(cells, onCellChange = { ndx, value ->
                    cells[ndx] = cells[ndx].copy(value = value, isPreset = true)
                })
            }
            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxHeight()
                    .weight(0.33f)
                    .border(width = 1.dp, color = Color.Black)
            ) {
                DrawButtons(
                    onRun = {
                        saveCellsForReload(cells, origninalCells)
                        scope.launch {
                            solve(cells, false) {
                                cells[it.first] = cells[it.first].copy(value = it.second.toString())
                            }
                        }
                    }, onAnimate = {
                        saveCellsForReload(cells, origninalCells)
                        scope.launch {
                            solve(cells, true) {
                                cells[it.first] = cells[it.first].copy(value = it.second.toString())
                            }
                        }
                    }, onReset = {
                        cells.replaceAll { CellState(".", isEditing = false, isPreset = false) }
                    }, onLoad = {
                        openFileAction = true
                    },onReload = {
                        origninalCells.forEachIndexed { ndx, value ->
                            cells[ndx] = value.copy()
                        }
                    })
            }
        }
    }
}

private fun saveCellsForReload(
    cells: SnapshotStateList<CellState>,
    origninalCells: SnapshotStateList<CellState>
) {
    cells.forEachIndexed { ndx, value ->
        origninalCells[ndx] = value.copy()
    }
}

@Composable
fun DrawButtons(onRun: () -> Unit,
                onAnimate: () -> Unit,
                onReset: () -> Unit,
                onReload: () -> Unit,
                onLoad: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onRun,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Run")
        }
        Button(
            onClick = onAnimate,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Animate")
        }
        Button(
            onClick = onReset,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Reset")
        }
        Button(
            onClick = onLoad,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Load CSV")
        }
        Button(
            onClick = onReload,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Reload")
        }
    }
}

@Composable
fun DrawGrid(cells: List<CellState>, onCellChange: (Int, String) -> Unit) {

    val innerBoxSize = 60.dp
    val textFontSize = 25.sp
    val editingFontSize = 18.sp
    var counter by remember { mutableStateOf(0) }

    val initialListValues = mutableListOf<Boolean>()
    initializeIsEditing(initialListValues)
    Column(modifier = Modifier.padding(10.dp)) {
        val isEditing = remember { initialListValues.toMutableStateList() }

        for (ndx in 1..3) {
            Row(modifier = Modifier.border(1.dp, color = Color.Black)) {
                for(ndx1 in 1..3) {
                    Column (modifier = Modifier.border(1.dp, color = Color.Black)) {
                        for (ndx2 in 1..3) {
                            Row {
                                for(ndx3 in 1..3) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .border(width = 1.dp, color = Color.LightGray)
                                                .width(innerBoxSize)
                                                .height(innerBoxSize),
                                            contentAlignment = Alignment.Center
                                        ) {

                                            val currentBox by remember { mutableStateOf(counter++) }

                                            val currentCell = cells[currentBox]

                                            if (isEditing[currentBox]) {
                                                TextField(
                                                    value = currentCell.value,
                                                    onValueChange = {
                                                        onCellChange(currentBox, it)
                                                    },
                                                    modifier = Modifier
                                                        .align(Alignment.Center),
                                                    textStyle = TextStyle(
                                                        fontSize = editingFontSize,
                                                        color = if (cells[currentBox].isValid) Color.Blue else Color.Red
                                                    ),
                                                )
                                            } else {
                                                Text(
                                                    text = currentCell.value,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.align(Alignment.Center)
                                                        .clickable {
                                                            isEditing.replaceAll { false }
                                                            isEditing[currentBox] = true
                                                        },
                                                    fontSize = textFontSize,
                                                    color = if (cells[currentBox].isPreset) Color.Blue else Color.Red,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun openFileDialog(
    window: ComposeWindow,
    title: String,
    allowedExtensions: List<String>,
    allowMultiSelection: Boolean = false
): Array<File> {
    return FileDialog(window, title, FileDialog.LOAD).apply {
        isMultipleMode = allowMultiSelection

        // windows
        file = allowedExtensions.joinToString(separator) { "*$it" } // e.g. '*.jpg'

        // linux
        setFilenameFilter { _, name ->
            allowedExtensions.any {
                name.endsWith(it)
            }
        }

        isVisible = true
    }.files
}


suspend fun solve(cells: MutableList<CellState>, animate: Boolean = true, onUpdate: (Pair<Int, Char>) -> Unit) {
    val boardArray: Array<CharArray> = to2DArray(cells)
    val solution = Solution(boardArray)
    solution.solve(animate).collect { pair ->
        onUpdate(pair)
    }

    println("finished")
}