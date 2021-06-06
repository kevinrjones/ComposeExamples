package com.knowledgespike.classfileviewer

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.desktop.AppManager
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.v1.KeyStroke
import androidx.compose.ui.window.v1.Menu
import androidx.compose.ui.window.v1.MenuBar
import androidx.compose.ui.window.v1.MenuItem
import java.io.File
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import java.nio.ByteBuffer

fun main() {

    System.setProperty("apple.laf.useScreenMenuBar", "true")

    val openFileAction = mutableStateOf(false)


    AppManager.setMenu(
        MenuBar(
            Menu(
                name = "File",
                MenuItem(
                    name = "Open",
                    onClick = { openFileAction.value = true },
                    shortcut = KeyStroke(Key.O)
                ),
            ),
        )
    )
    Window(
        title = "Class File Viewer",
        size = IntSize(900, 800)
    ) {
        val panelState = remember { PanelState() }
        var file: File? by remember { mutableStateOf(null) }
        var classFileParser: ClassFileParser? by remember { mutableStateOf(null) }
        val fileData by remember { mutableStateOf(StringBuilder()) }
        var cardContent: ConstantPoolDisplay? by remember { mutableStateOf(null) }


        if (openFileAction.value) {
            val files = openFileDialog(LocalAppWindow.current.window, "Open File", listOf(".class"))
            file = if (files.isEmpty()) null else files.first()
            openFileAction.value = false
            val bytes = parseFile(file, fileData)
            classFileParser = ClassFileParser()
            classFileParser?.parse(bytes)
        }


        // get the size of the panel and use that in the layout
        val animatedSize = if (panelState.splitter.isResizing) {
            if (panelState.isExpanded) panelState.expandedSize else panelState.collapsedSize
        } else {
            animateDpAsState(
                if (panelState.isExpanded) panelState.expandedSize else panelState.collapsedSize,
                SpringSpec(stiffness = Spring.StiffnessLow)
            ).value
        }
        MaterialTheme {
            VerticalSplittable(Modifier.fillMaxSize(),
                panelState.splitter,
                onResize = {
                    panelState.expandedSize =
                        (panelState.expandedSize + it).coerceAtLeast(panelState.expandedSizeMin)
                }
            ) {
                ResizablePanel(Modifier.width(animatedSize).fillMaxHeight(), panelState) {
                    Column {
                        FileViewTabView()
//                        FileView(fileData.toString())
                        FileView(classFileParser?.getGroupedBytes() ?: arrayOf(""),
                            onClick = { evt ->
                                val parseData = getParsedEntry(classFileParser, evt)
                                classFileParser?.let {
                                    cardContent = getCardContent(it, parseData)

                                    println(cardContent.toString())
                                }
                            })

                    }
                }
                DeconstructedClassFile(classFileParser, cardContent)
            }
        }
    }
}

fun getParsedEntry(classFileParser: ClassFileParser?, offset: Int): ClassfileDisplayDetails {
    if (classFileParser == null) throw Exception("Class file not parsed")

    val classFileParseData = classFileParser.parsedStructure.first {
        offset <= it.limit
    }
    return classFileParseData
}

private fun parseFile(
    file: File?,
    fileData: StringBuilder
): ByteArray {
    val stream = file?.inputStream()
    var bytes: ByteArray? = null

    stream?.use {
        while (it.available() > 0) {
            bytes = it.readAllBytes()
        }
    }
    bytes?.let {
        it.forEach {
            val hexValue = String.format("%02x", it)
            fileData.append(hexValue)
            fileData.append(" ")
        }
    }
    return bytes ?: ByteArray(0)
}

@Composable
fun FileViewTabView() = Surface {
    Row(
        Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "File",
            color = LocalContentColor.current.copy(alpha = 0.60f),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun FileView(fileData: String) = Surface(
    modifier = Modifier.fillMaxSize()
) {

    Box(modifier = Modifier.background(AppTheme.colors.backgroundLight)) {
        val fontSize = 14.sp


        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                fileData,
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = fontSize,
                modifier = Modifier.verticalScroll(scrollState)
            )
            VerticalScrollbar(
                rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().background(Color.LightGray)
            )
        }
    }
}


@Composable
fun FileView(data: Array<String>, onClick: (Int) -> Unit) = Surface(
    modifier = Modifier.fillMaxSize()
) {


    Box(modifier = Modifier.background(AppTheme.colors.backgroundLight)) {
        val fontSize = 16.sp


        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            val annotatedText = buildAnnotatedString {
                data.forEachIndexed { ndx, item ->
                    if (ndx % 2 == 0)
                        withStyle(
                            style = SpanStyle(
                                color = Color.White, fontFamily = FontFamily.Monospace,
                                fontSize = fontSize
                            )
                        ) {
                            append(data[ndx])
                        }
                    else {
                        withStyle(
                            style = SpanStyle(
                                color = Color.LightGray,
                                background = Color.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = fontSize
                            )
                        ) {
                            append(data[ndx])
                        }
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = fontSize
                        )
                    ) {
                        append(" ")
                    }
                }
            }
            ClickableText(
                annotatedText,
                modifier = Modifier.verticalScroll(scrollState),
                onClick = onClick,
            )
            VerticalScrollbar(
                rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().background(Color.LightGray)
            )
        }
    }
}


@Composable
fun DeconstructedClassFile(classFileParser: ClassFileParser?, cardContent: ConstantPoolDisplay?) {
    val fontSize = 14.sp

    Box(Modifier.fillMaxSize().background(AppTheme.colors.backgroundLight).padding(10.dp)) {
        val scrollState = rememberLazyListState()

        classFileParser?.let { cfp ->

            val attributes = getAttributesForView(cfp)
            val sourceAttribute = attributes.find { it.first == "Source File" }

            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        "Check bytes ${cfp.magic.toHex()}",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSize
                    )
                }
                item {
                    Text(
                        "Minor Version: ${cfp.minorVersionInt}, Major Version: ${cfp.majorVersionInt}",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSize
                    )
                }
                item {
                    Text(
                        "Flags: 0x${cfp.accessFlags.toHex().replace(" ", "")}",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSize
                    )
                }
                item {
                    Text(
                        "Constant Pool Count: ${cfp.constantPoolCountInt}",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSize
                    )
                }
                item {
                    Text(
                        "Interfaces: ${cfp.interfacesCountInt}, fields: ${cfp.fieldsCountInt}, methods: ${cfp.methodsCountInt}, attributes: ${cfp.attributesCountInt}",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSize
                    )
                }

                item {
                    Text(
                        "This class: #${cfp.thisClassInt} ${getStringFromConstantPool(cfp, cfp.thisClassInt)} ",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSize
                    )
                }

                item {
                    Text(
                        "Super class: #${cfp.superClassInt} ${getStringFromConstantPool(cfp, cfp.superClassInt)} ",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSize
                    )
                }

                item {
                    Text(
                        "Source file: ${sourceAttribute?.second} ",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSize
                    )
                }

                if (cardContent != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            elevation = 12.dp,
                            border = BorderStroke(2.dp, AppTheme.colors.backgroundDark)
                        ) {
                            Column(modifier = Modifier.background(Color.White)
                                .padding(10.dp)) {
                                Text(cardContent.title,
                                    color = Color.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 16.sp)

                                Spacer(modifier = Modifier.padding(5.dp))

                                for (content in cardContent.content) {
                                    Text(content,
                                        color = Color.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = fontSize)
                                    Spacer(modifier = Modifier.padding(2.dp))
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}


fun getAttributesForView(classFileParser: ClassFileParser): List<Pair<String, String>> {

    val attributes = mutableListOf<Pair<String, String>>()

    for (attribute in classFileParser.attributes) {
        val name = getStringFromConstantPool(classFileParser, attribute.attributeNameIndexInt)

        val nameValuePair = when (name) {
            "SourceFile" -> {
                val sourceFileIndex = ByteBuffer.wrap(attribute.info.toByteArray()).short
                val sourceFileName = getStringFromConstantPool(classFileParser, sourceFileIndex)
                Pair("Source File", sourceFileName)
            }
            "ConstantValue" -> {
                val constantFileIndex = ByteBuffer.wrap(attribute.info.toByteArray()).short
                val constantValueName = getStringFromConstantPool(classFileParser, constantFileIndex)
                Pair("Constant Value", constantValueName)
            }
            "Code" -> {
                Pair("Code", attribute.getBytes().toHex())
            }
            "StackMapTable" -> {
                Pair("Stack Map Table", attribute.getBytes().toHex())
            }
            "Exceptions" -> {
                Pair("Exceptions", attribute.getBytes().toHex())
            }
            "InnerClasses" -> {
                Pair("Inner Classes", attribute.getBytes().toHex())
            }
            "EnclosingMethod" -> {
                Pair("Enclosing Method", attribute.getBytes().toHex())
            }
            "Synthetic" -> {
                Pair("Synthetic", "")
            }
            "Signature" -> {
                Pair("Signature", attribute.getBytes().toHex())
            }
            "SourceDebugExtension" -> {
                Pair("Source Debug Extension", attribute.getBytes().toHex())
            }
            "LineNumberTable" -> {
                Pair("Line Number Table", attribute.getBytes().toHex())
            }
            "LocalVariableTable" -> {
                Pair("Local Variable Table", attribute.getBytes().toHex())
            }
            "LocalVariableTypeTable" -> {
                Pair("Local Variable Type Table", attribute.getBytes().toHex())
            }
            "Deprecated" -> {
                Pair("Deprecated", "")
            }
            "RuntimeVisibleAnnotations" -> {
                Pair("Runtime Visible Annotations", attribute.getBytes().toHex())
            }
            "RuntimeInvisibleAnnotations" -> {
                Pair("Runtime Invisible Annotations", attribute.getBytes().toHex())
            }
            "RuntimeVisibleParameterAnnotations" -> {
                Pair("Runtime Visible Parameter Annotations", attribute.getBytes().toHex())
            }
            "RuntimeInvisibleParameterAnnotations" -> {
                Pair("Runtime Invisible Parameter Annotations", attribute.getBytes().toHex())
            }
            "RuntimeVisibleTypeAnnotations" -> {
                Pair("Runtime Visible Type Annotations", attribute.getBytes().toHex())
            }
            "RuntimeInvisibleTypeAnnotations" -> {
                Pair("Runtime Invisible Type Annotations", attribute.getBytes().toHex())
            }
            "AnnotationDefault" -> {
                Pair("Annotation Default", attribute.getBytes().toHex())
            }
            "BootstrapMethods" -> {
                Pair("Bootstrap Methods", attribute.getBytes().toHex())
            }
            "MethodParameters" -> {
                Pair("Method Parameters", attribute.getBytes().toHex())
            }
            "Module" -> {
                Pair("Module", attribute.getBytes().toHex())
            }
            "ModulePackages" -> {
                Pair("Module Packages", attribute.getBytes().toHex())
            }
            "ModuleMainClass" -> {
                Pair("Module Main Class", attribute.getBytes().toHex())
            }
            "NestHost" -> {
                Pair("Nest Host", attribute.getBytes().toHex())
            }
            "NestMembers" -> {
                Pair("Nest Members", attribute.getBytes().toHex())
            }
            "Record" -> {
                Pair("Record", attribute.getBytes().toHex())
            }
            else -> Pair(name, "Unknown")
        }
        attributes.add(nameValuePair)
    }

    return attributes

}


