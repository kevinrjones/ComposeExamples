package com.knowledgespike.classfileviewer

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

class PanelState {
    val collapsedSize = 24.dp
    var expandedSize by mutableStateOf(300.dp)
    val expandedSizeMin = 90.dp
    var isExpanded by mutableStateOf(true)
    val splitter = SplitterState()
}

/**
 * Contains an arrow to collapse or expand the panel and displays the content passed in as a composable
 */
@Composable
fun ResizablePanel(
    modifier: Modifier,
    state: PanelState,
    content: @Composable () -> Unit,
) {
    val alpha = animateFloatAsState(if (state.isExpanded) 1f else 0f, SpringSpec(stiffness = Spring.StiffnessLow))

    Box(modifier) {
        Box(Modifier.fillMaxSize().graphicsLayer (alpha = alpha.value)) {
            content()
        }

        Icon(
            if (state.isExpanded) Icons.Default.ArrowBack else Icons.Default.ArrowForward,
            "Icon",
            tint = LocalContentColor.current,
            modifier = Modifier
                .padding(top = 4.dp)
                .width(24.dp)
                .clickable {
                    state.isExpanded = !state.isExpanded
                }
                .padding(4.dp)
                .align(Alignment.TopEnd)
        )
    }
}
