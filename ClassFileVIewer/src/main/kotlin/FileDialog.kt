package com.knowledgespike.classfileviewer

import androidx.compose.desktop.ComposeWindow
import androidx.compose.runtime.Composable
import java.awt.FileDialog
import java.io.File
import javax.swing.JFileChooser

import javax.swing.filechooser.FileNameExtensionFilter

import javax.swing.filechooser.FileSystemView


var separator = System.getProperty("file.separator")

/**
 * Uses AWT FileDialog
 */
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


/**
 * Use Swing JFileChooser
 */
fun openJFileDialog(
    window: ComposeWindow,
    title: String,
    allowedExtensions: List<String>,
    allowMultiSelection: Boolean = false
): Array<File> {
    val jfc = JFileChooser(FileSystemView.getFileSystemView().homeDirectory).apply {
        dialogTitle = title
        isAcceptAllFileFilterUsed = false
        val filter = FileNameExtensionFilter("Class Files", allowedExtensions.joinToString(",") {
            if (it.startsWith(".")) {
                it.substring(1)
            } else {
                it
            }
        })
        isMultiSelectionEnabled = allowMultiSelection
        addChoosableFileFilter(filter)
    }

    val returnValue = jfc.showOpenDialog(window)
    if (returnValue == JFileChooser.APPROVE_OPTION) {
        return jfc.selectedFiles
    } else {
        return arrayOf()
    }
}