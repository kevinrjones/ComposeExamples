package com.knowledgespike.classfileviewer

import java.nio.ByteBuffer

class ClassFileParser {

    val magic = ByteArray(4)
    val minorVersion = ByteArray(2)
    var minorVersionInt: Short = 0
    val majorVersion = ByteArray(2)
    var majorVersionInt: Short = 0
    val constantPoolCount = ByteArray(2)
    var constantPoolCountInt: Short = 0
    val constantPoolEntries = mutableListOf<ConstantPool>()
    val accessFlags = ByteArray(2)
    val thisClass = ByteArray(2)
    var thisClassInt: Short = 0
    val superClass = ByteArray(2)
    var superClassInt: Short = 0

    val interfacesCount = ByteArray(2)
    var interfacesCountInt: Short = 0
    val interfaces = mutableListOf<Short>()

    val fieldsCount = ByteArray(2)
    var fieldsCountInt: Short = 0
    val fields = mutableListOf<FieldMethodInfo>()

    val methodsCount = ByteArray(2)
    var methodsCountInt: Short = 0
    val methods = mutableListOf<FieldMethodInfo>()

    val attributesCount = ByteArray(2)
    var attributesCountInt: Short = 0
    val attributes = mutableListOf<AttributeInfo>()

    val parsedStructure = mutableListOf<ClassfileDisplayDetails>()

    private var hasParsed = false

    fun parse(classFile: ByteArray) {
        try {
            hasParsed = false
            if (classFile.size == 0) return

            var offset = 0
            var limit = offset + magic.size
            for (ndx in offset..limit - 1) {
                magic[ndx] = classFile[ndx]
            }

            parsedStructure.add(
                ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                    limit * 3,
                    "Magic",
                    magic,
                    magic.toHex()
                )
            )

            offset = limit
            limit = offset + minorVersion.size
            for (ndx in offset..limit - 1) {
                minorVersion[ndx - offset] = classFile[ndx]
            }

            var wrapper: ByteBuffer = ByteBuffer.wrap(minorVersion)
            minorVersionInt = wrapper.getShort()

            offset = limit
            limit = offset + majorVersion.size
            for (ndx in offset..limit - 1) {
                majorVersion[ndx - offset] = classFile[ndx]
            }

            wrapper = ByteBuffer.wrap(majorVersion)
            majorVersionInt = wrapper.getShort()

            val versionBytes = ByteArray(4)
            System.arraycopy(minorVersion, 0, versionBytes, 0, 2)
            System.arraycopy(majorVersion, 0, versionBytes, 2, 2)

            parsedStructure.add(
                ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                    limit * 3,
                    "Version",
                    versionBytes,
                    "${majorVersionInt}.${minorVersionInt}"
                )
            )

            offset = limit
            limit = offset + constantPoolCount.size
            for (ndx in offset..limit - 1) {
                constantPoolCount[ndx - offset] = classFile[ndx]
            }

            wrapper = ByteBuffer.wrap(constantPoolCount)
            constantPoolCountInt = wrapper.getShort()

            parsedStructure.add(
                ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                    limit * 3,
                    "Constant Pool Count",
                    constantPoolCount,
                    "$constantPoolCountInt"
                )
            )

            var cpNdx: Short = 1

            while (cpNdx < constantPoolCountInt) {
                offset = limit
                val constantPoolTag = ByteArray(1)
                limit = offset + constantPoolTag.size
                for (ndx in offset..limit - 1) {
                    constantPoolTag[ndx - offset] = classFile[ndx]
                }

                offset = limit

                val tag = constantPoolTag[0].toInt()

                lateinit var constant: ConstantPool
                limit = when (tag) {
                    1 -> {
                        constant = ConstantPoolUtf8(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    3 -> {
                        constant = ConstantInteger(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    4 -> {
                        constant = ConstantFloat(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    5 -> {
                        constant = ConstantLong(cpNdx)
                        constantPoolEntries.add(constant)
                        cpNdx++
                        constant.parse(classFile, offset)
                    }
                    6 -> {
                        constant = ConstantDouble(cpNdx)
                        constantPoolEntries.add(constant)
                        cpNdx++
                        constant.parse(classFile, offset)
                    }
                    7 -> {
                        constant = ConstantClass(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    8 -> {
                        constant = ConstantString(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    9 -> {
                        constant = ConstantFieldRef(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    10 -> {
                        constant = ConstantMethodRef(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    11 -> {
                        constant = ConstantInterfaceRef(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    12 -> {
                        constant = ConstantNameAndType(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    15 -> {
                        constant = ConstantMethodHandle(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    16 -> {
                        constant = ConstantMethodType(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    17 -> {
                        constant = ConstantDynamic(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    18 -> {
                        constant = ConstantInvokeDynamic(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    19 -> {
                        constant = ConstantModule(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    20 -> {
                        constant = ConstantPackage(cpNdx)
                        constantPoolEntries.add(constant)
                        constant.parse(classFile, offset)
                    }
                    else -> throw Exception("Unknown Tag: $tag")
                }

//                parsedStructure.add(ClassfileDisplayDetails.BasicClassfileDisplayDetails(limit * 3, "Version", "${majorVersionInt}.${minorVersionInt}"))

                parsedStructure.add(constant.getDisplayDetails())
                cpNdx++
            }


            offset = limit
            limit = offset + accessFlags.size
            for (ndx in offset..limit - 1) {
                accessFlags[ndx - offset] = classFile[ndx]
            }

            parsedStructure.add(
                ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                    limit * 3,
                    "Access Flags",
                    accessFlags,
                    accessFlags.toHex()
                )
            )

            offset = limit
            limit = offset + thisClass.size
            for (ndx in offset..limit - 1) {
                thisClass[ndx - offset] = classFile[ndx]
            }

            wrapper = ByteBuffer.wrap(thisClass)
            thisClassInt = wrapper.getShort()

            parsedStructure.add(
                ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                    limit * 3,
                    "This Class",
                    thisClass,
                    thisClass.toHex()
                )
            )

            offset = limit
            limit = offset + superClass.size
            for (ndx in offset..limit - 1) {
                superClass[ndx - offset] = classFile[ndx]
            }

            wrapper = ByteBuffer.wrap(superClass)
            superClassInt = wrapper.getShort()

            parsedStructure.add(
                ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                    limit * 3,
                    "Super Class",
                    superClass,
                    superClass.toHex()
                )
            )

            offset = limit
            limit = offset + interfacesCount.size
            for (ndx in offset..limit - 1) {
                interfacesCount[ndx - offset] = classFile[ndx]
            }

            parsedStructure.add(
                ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                    limit * 3,
                    "Number of Interfaces",
                    interfacesCount,
                    "$interfacesCountInt"
                )
            )

            interfacesCountInt = ByteBuffer.wrap(interfacesCount).getShort()

            if (interfacesCountInt > 0) {
                for (interfaceNdx in 0..interfacesCountInt - 1) {
                    val interfacesIndexArray = ByteArray(2)
                    offset = limit
                    limit = offset + interfacesIndexArray.size
                    for (ndx in offset..limit - 1) {
                        interfacesIndexArray[ndx - offset] = classFile[ndx]
                    }
                    interfaces.add(ByteBuffer.wrap(interfacesIndexArray).getShort())
                    parsedStructure.add(
                        ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                            limit * 3,
                            "Interface $interfaceNdx",
                            interfacesIndexArray,
                            "$interfacesIndexArray"
                        )
                    )
                }
            }

            offset = limit
            limit = offset + fieldsCount.size
            for (ndx in offset..limit - 1) {
                fieldsCount[ndx - offset] = classFile[ndx]
            }
            parsedStructure.add(
                ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                    limit * 3,
                    "Number of Fields",
                    fieldsCount,
                    "$fieldsCount"
                )
            )

            fieldsCountInt = ByteBuffer.wrap(fieldsCount).getShort()
            if (fieldsCountInt > 0) {
                for (interfaceNdx in 0..fieldsCountInt - 1) {
                    offset = limit
                    val fieldInfo = FieldMethodInfo()
                    limit = fieldInfo.parse(classFile, offset)
                    fields.add(fieldInfo)
                    val bytesToAdd = fieldInfo.getBytes()
                    parsedStructure.add(
                        ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                            limit * 3,
                            "Field $interfaceNdx",
                            bytesToAdd,
                            bytesToAdd.toHex()
                        )
                    )
                }
            }

            offset = limit
            limit = offset + methodsCount.size
            for (ndx in offset..limit - 1) {
                methodsCount[ndx - offset] = classFile[ndx]
            }
            parsedStructure.add(
                ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                    limit * 3,
                    "Number of Methods",
                    methodsCount,
                    "$methodsCount"
                )
            )

            methodsCountInt = ByteBuffer.wrap(methodsCount).getShort()
            if (methodsCountInt > 0) {
                for (interfaceNdx in 0..methodsCountInt - 1) {
                    offset = limit
                    val methodInfo = FieldMethodInfo()
                    limit = methodInfo.parse(classFile, offset)
                    methods.add(methodInfo)
                    val bytesToAdd = methodInfo.getBytes()
                    parsedStructure.add(
                        ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                            limit * 3,
                            "Method $interfaceNdx",
                            bytesToAdd,
                            bytesToAdd.toHex()
                        )
                    )
                }
            }

            offset = limit
            limit = offset + attributesCount.size
            for (ndx in offset..limit - 1) {
                attributesCount[ndx - offset] = classFile[ndx]
            }

            parsedStructure.add(
                ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                    limit * 3,
                    "Number of Attributes",
                    attributesCount,
                    "$attributesCount"
                )
            )

            attributesCountInt = ByteBuffer.wrap(attributesCount).getShort()
            if (attributesCountInt > 0) {
                for (interfaceNdx in 0..attributesCountInt - 1) {
                    offset = limit
                    val attrInfo = AttributeInfo()
                    limit = attrInfo.parse(classFile, offset)
                    val bytesToAdd = attrInfo.getBytes()
                    parsedStructure.add(
                        ClassfileDisplayDetails.BasicClassfileDisplayDetails(
                            limit * 3,
                            "Attribute $interfaceNdx",
                            bytesToAdd,
                            bytesToAdd.toHex()
                        )
                    )
                    attributes.add(attrInfo)
                }
            }

            hasParsed = true
            require(limit == classFile.size)

        } catch (t: Throwable) {
            hasParsed = false
            t.printStackTrace()
        }
    }

    fun getGroupedBytes(): Array<String> {
        val data = mutableListOf<String>()

        parsedStructure.map {
            data.add(it.bytes.toHex())
        }

        return data.toTypedArray()
    }
}

fun ByteArray.toHex(): String {
    val sb = StringBuilder(this.size * 2)
    for (b in this) sb.append(String.format("%02x ", b))
    return sb.toString().trimEnd()
}


sealed class ClassfileDisplayDetails(
) {
    abstract val limit: Int
    abstract val title: String
    abstract val bytes: ByteArray
    abstract val content: String

    data class BasicClassfileDisplayDetails(
        override val limit: Int,
        override val title: String,
        override val bytes: ByteArray,
        override val content: String
    ) : ClassfileDisplayDetails()

    // utf-8
    data class UtfConstantPoolDisplayDetails(
        override val limit: Int,
        override val title: String,
        override val bytes: ByteArray,
        override val content: String,
        val constantPoolIndex: Short
    ) : ClassfileDisplayDetails()


    // methodhandle,
    // dynamic, invokeDynamic
    data class BasicConstantPoolDisplayDetails(
        override val limit: Int,
        override val title: String,
        override val bytes: ByteArray,
        override val content: String,
        val constantPoolIndex: Short
    ) : ClassfileDisplayDetails()

    // integer, float, double, long
    data class ConstantValuePoolDisplayDetails(
        override val limit: Int,
        override val title: String,
        override val bytes: ByteArray,
        override val content: String,
        val constantPoolIndex: Short
    ) : ClassfileDisplayDetails()

    // string, package, module, methodtype
    // Class
    data class NamedConstantPoolDisplayDetails(
        override val limit: Int,
        override val title: String,
        override val bytes: ByteArray,
        override val content: String,
        val constantPoolIndex: Short,
        val constantPoolIndexLink: Short
    ) : ClassfileDisplayDetails()

    // NameAndType, MethodRef
    data class DescriptorConstantPoolDisplayDetails(
        override val limit: Int,
        override val title: String,
        override val bytes: ByteArray,
        override val content: String,
        val constantPoolIndex: Short,
        val constantPoolIndexLink: Short,
        val descriptorPoolIndexLink: Short
    ) : ClassfileDisplayDetails()

    // fieldref, interfaceref
    data class RefConstantPoolDisplayDetails(
        override val limit: Int,
        override val title: String,
        override val bytes: ByteArray,
        override val content: String,
        val constantPoolIndex: Short,
        val classIndex: Short,
        val nameAndTypeIndex: Short
    ) : ClassfileDisplayDetails()
}