package com.knowledgespike.classfileviewer

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerMoveFilter
import java.awt.Cursor


/**
 * Composes over the 'pointerMoveFilter` to provide a stateful modifier of the filter so remembers the
 * cursor for each element it composes over
 */
fun Modifier.cursorForHorizontalResize(): Modifier = composed {
    var isHover by remember { mutableStateOf(false) }

    if (isHover) {
        LocalAppWindow.current.window.cursor = Cursor(Cursor.E_RESIZE_CURSOR)
    } else {
        LocalAppWindow.current.window.cursor = Cursor.getDefaultCursor()
    }

    pointerMoveFilter(
        onEnter = { isHover = true; true },
        onExit = { isHover = false; true }
    )
}