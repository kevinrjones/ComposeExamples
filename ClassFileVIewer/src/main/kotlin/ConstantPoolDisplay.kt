package com.knowledgespike.classfileviewer

fun getCardContent(classFileParser: ClassFileParser, parseData: ClassfileDisplayDetails): ConstantPoolDisplay {

    parseData.title

    val constantPoolDsiplay: ConstantPoolDisplay = when (parseData) {
        is ClassfileDisplayDetails.ConstantValuePoolDisplayDetails -> getConstantPoolValueContent(
            classFileParser,
            parseData
        )
        is ClassfileDisplayDetails.UtfConstantPoolDisplayDetails -> getUtfConstantPoolContent(
            classFileParser,
            parseData
        )
        is ClassfileDisplayDetails.BasicConstantPoolDisplayDetails -> getBasicConstantPoolContent(
            classFileParser,
            parseData
        )
        is ClassfileDisplayDetails.DescriptorConstantPoolDisplayDetails -> getDescriptorConstantPoolContent(
            classFileParser,
            parseData
        )
        is ClassfileDisplayDetails.NamedConstantPoolDisplayDetails -> getNamedConstantPoolContent(
            classFileParser,
            parseData
        )
        is ClassfileDisplayDetails.RefConstantPoolDisplayDetails -> getRefConstantPoolContent(
            classFileParser,
            parseData
        )
        else -> ConstantPoolDisplay("Not in constant pool", listOf())
    }

    return constantPoolDsiplay
}

fun getConstantPoolValueContent(
    classFileParser: ClassFileParser,
    constantPoolDetails: ClassfileDisplayDetails.ConstantValuePoolDisplayDetails
): ConstantPoolDisplay {
    val name = getStringFromConstantPool(classFileParser, constantPoolDetails.constantPoolIndex)


    return ConstantPoolDisplay(
        constantPoolDetails.title,
        listOf(
            "#${constantPoolDetails.constantPoolIndex}",
            name,
            constantPoolDetails.content
        )
    )
}

fun getRefConstantPoolContent(
    classFileParser: ClassFileParser,
    constantPoolDetails: ClassfileDisplayDetails.RefConstantPoolDisplayDetails
): ConstantPoolDisplay {
    val name = getStringFromConstantPool(classFileParser, constantPoolDetails.constantPoolIndex)
    val className = getStringFromConstantPool(classFileParser, constantPoolDetails.classIndex)
    val nameAndType = getStringFromConstantPool(classFileParser, constantPoolDetails.nameAndTypeIndex)


    return ConstantPoolDisplay(
        "${constantPoolDetails.title} (#${constantPoolDetails.constantPoolIndex})",
        listOf(
            "#${constantPoolDetails.classIndex}.#${constantPoolDetails.nameAndTypeIndex}",
            name,
            "$className:$nameAndType"
        )
    )
}


fun getNamedConstantPoolContent(
    classFileParser: ClassFileParser,
    constantPoolDetails: ClassfileDisplayDetails.NamedConstantPoolDisplayDetails
): ConstantPoolDisplay {
    val descriptor = getStringFromConstantPool(classFileParser, constantPoolDetails.constantPoolIndexLink)


    return ConstantPoolDisplay(
        "${constantPoolDetails.title} (#${constantPoolDetails.constantPoolIndex})",
        listOf(
            "#${constantPoolDetails.constantPoolIndexLink}",
            descriptor
        )
    )
}

fun getDescriptorConstantPoolContent(
    classFileParser: ClassFileParser,
    constantPoolDetails: ClassfileDisplayDetails.DescriptorConstantPoolDisplayDetails
): ConstantPoolDisplay {

    val name = getStringFromConstantPool(classFileParser, constantPoolDetails.constantPoolIndexLink)
    val type = getStringFromConstantPool(classFileParser, constantPoolDetails.descriptorPoolIndexLink)


    return ConstantPoolDisplay(
        "${constantPoolDetails.title} (#${constantPoolDetails.constantPoolIndex})",
        listOf(
            "#${constantPoolDetails.constantPoolIndexLink};#${constantPoolDetails.descriptorPoolIndexLink}",
            "${name};${type}"
        )
    )
}

fun getBasicConstantPoolContent(
    classFileParser: ClassFileParser,
    constantPoolDetails: ClassfileDisplayDetails.BasicConstantPoolDisplayDetails
): ConstantPoolDisplay {

    val text = getStringFromConstantPool(classFileParser, constantPoolDetails.constantPoolIndex)
    return ConstantPoolDisplay(constantPoolDetails.title, listOf(text))
}

fun getUtfConstantPoolContent(
    classFileParser: ClassFileParser,
    constantPoolDetails: ClassfileDisplayDetails.UtfConstantPoolDisplayDetails
): ConstantPoolDisplay {

    val text = getStringFromConstantPool(classFileParser, constantPoolDetails.constantPoolIndex)
    return ConstantPoolDisplay(
        "${constantPoolDetails.title} (#${constantPoolDetails.constantPoolIndex})",
        listOf(text)
    )
}

data class ConstantPoolDisplay(val title: String, val content: List<String>)