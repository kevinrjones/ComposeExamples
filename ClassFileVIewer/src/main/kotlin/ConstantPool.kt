package com.knowledgespike.classfileviewer

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

sealed class ConstantPool(val constantPoolIndex: Short) {
    abstract val constantPoolType: Int
    abstract fun parse(classFile: ByteArray, offset: Int): Int
    abstract fun getDisplayDetails(): ClassfileDisplayDetails
    abstract fun getBytes(): ByteArray
}

class ConstantPoolUtf8(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 1
    val length = ByteArray(2)
    var lengthInt: Short = 0
    lateinit var strValue: String
    var limit: Int = 0

    private var utf8Bytes = ByteArray(0)

    override fun parse(classFile: ByteArray, offset: Int): Int {
        var localOffset = offset


        limit = localOffset + length.size
        for (ndx in localOffset..limit - 1) {
            length[ndx - localOffset] = classFile[ndx]
        }

        lengthInt = ByteBuffer.wrap(length).short

        utf8Bytes = ByteArray(lengthInt.toInt())

        localOffset = limit
        limit = localOffset + lengthInt
        for (ndx in localOffset..limit - 1) {
            utf8Bytes[ndx - localOffset] = classFile[ndx]
        }

        strValue = String(utf8Bytes, StandardCharsets.UTF_8)

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.UtfConstantPoolDisplayDetails(
            limit * 3,
            "UTF8",
            getBytes(),
            strValue,
            constantPoolIndex
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(utf8Bytes.size + 3)

        allBytes[0] = 1
        System.arraycopy(length, 0, allBytes, 1, length.size)
        System.arraycopy(utf8Bytes, 0, allBytes, 3, utf8Bytes.size)
        return allBytes
    }
}

class ConstantClass(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 7
    val nameIndex = ByteArray(2)
    var nameIndexInt: Short = 0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        val localOffset = offset
        limit = localOffset + nameIndex.size
        for (ndx in localOffset..limit - 1) {
            nameIndex[ndx - localOffset] = classFile[ndx]
        }

        nameIndexInt = ByteBuffer.wrap(nameIndex).short

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.NamedConstantPoolDisplayDetails(
            limit * 3,
            "Class file",
            getBytes(),
            "#${constantPoolIndex}",
            constantPoolIndex,
            nameIndexInt
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(3)

        allBytes[0] = 7
        System.arraycopy(nameIndex, 0, allBytes, 1, 2)
        return allBytes
    }
}

class ConstantNameAndType(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 12
    val nameIndex = ByteArray(2)
    var nameIndexInt: Short = 0

    val descriptorIndex = ByteArray(2)
    var descriptorIndexInt: Short = 0

    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        var localOffset = offset
        limit = localOffset + nameIndex.size
        for (ndx in localOffset..limit - 1) {
            nameIndex[ndx - localOffset] = classFile[ndx]
        }
        nameIndexInt = ByteBuffer.wrap(nameIndex).short

        localOffset = limit
        limit = localOffset + descriptorIndex.size

        for (ndx in localOffset..limit - 1) {
            descriptorIndex[ndx - localOffset] = classFile[ndx]
        }
        descriptorIndexInt = ByteBuffer.wrap(descriptorIndex).short

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {

        return ClassfileDisplayDetails.DescriptorConstantPoolDisplayDetails(
            limit * 3,
            "Name and Type",
            getBytes(),
            "#${constantPoolIndex}",
            constantPoolIndex,
            nameIndexInt,
            descriptorIndexInt
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(5)

        allBytes[0] = 12

        System.arraycopy(nameIndex, 0, allBytes, 1, 2)
        System.arraycopy(descriptorIndex, 0, allBytes, 3, 2)

        return allBytes
    }
}

class ConstantMethodRef(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 10
    val classIndex = ByteArray(2)
    var classIndexInt: Short = 0
    val nameAndTypeIndex = ByteArray(2)
    var nameAndTypeIndexInt: Short = 0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        var localOffset = offset
        limit = localOffset + classIndex.size
        for (ndx in localOffset..limit - 1) {
            classIndex[ndx - localOffset] = classFile[ndx]
        }

        classIndexInt = ByteBuffer.wrap(classIndex).short

        localOffset = limit
        limit = localOffset + nameAndTypeIndex.size

        for (ndx in localOffset..limit - 1) {
            nameAndTypeIndex[ndx - localOffset] = classFile[ndx]
        }
        nameAndTypeIndexInt = ByteBuffer.wrap(nameAndTypeIndex).short

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.DescriptorConstantPoolDisplayDetails(
            limit * 3,
            "Method Reference",
            getBytes(),
            "#${constantPoolIndex}",
            constantPoolIndex,
            classIndexInt,
            nameAndTypeIndexInt
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(5)

        allBytes[0] = 10

        System.arraycopy(classIndex, 0, allBytes, 1, 2)
        System.arraycopy(nameAndTypeIndex, 0, allBytes, 3, 2)

        return allBytes
    }
}

class ConstantFieldRef(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 9
    val classIndex = ByteArray(2)
    var classIndexInt: Short = 0
    val nameAndTypeIndex = ByteArray(2)
    var nameAndTypeIndexInt: Short = 0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        var localOffset = offset
        limit = localOffset + classIndex.size
        for (ndx in localOffset..limit - 1) {
            classIndex[ndx - localOffset] = classFile[ndx]
        }
        classIndexInt = ByteBuffer.wrap(classIndex).short

        localOffset = limit
        limit = localOffset + nameAndTypeIndex.size

        for (ndx in localOffset..limit - 1) {
            nameAndTypeIndex[ndx - localOffset] = classFile[ndx]
        }
        nameAndTypeIndexInt = ByteBuffer.wrap(nameAndTypeIndex).short

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.RefConstantPoolDisplayDetails(
            limit * 3,
            "Field Reference",
            getBytes(),
            "#${constantPoolIndex}",
            constantPoolIndex,
            classIndexInt,
            nameAndTypeIndexInt
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(5)

        allBytes[0] = 9

        System.arraycopy(classIndex, 0, allBytes, 1, 2)
        System.arraycopy(nameAndTypeIndex, 0, allBytes, 3, 2)

        return allBytes
    }
}

class ConstantInterfaceRef(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 11
    val classIndex = ByteArray(2)
    var classIndexInt: Short = 0
    val nameAndTypeIndex = ByteArray(2)
    var nameAndTypeIndexInt: Short = 0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        var localOffset = offset
        limit = localOffset + classIndex.size
        for (ndx in localOffset..limit - 1) {
            classIndex[ndx - localOffset] = classFile[ndx]
        }
        classIndexInt = ByteBuffer.wrap(classIndex).short

        localOffset = limit
        limit = localOffset + nameAndTypeIndex.size

        for (ndx in localOffset..limit - 1) {
            nameAndTypeIndex[ndx - localOffset] = classFile[ndx]
        }
        nameAndTypeIndexInt = ByteBuffer.wrap(nameAndTypeIndex).short

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.RefConstantPoolDisplayDetails(
            limit * 3,
            "Interface Reference",
            getBytes(),
            "#${constantPoolIndex}",
            constantPoolIndex,
            classIndexInt,
            nameAndTypeIndexInt
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(5)

        allBytes[0] = 11

        System.arraycopy(classIndex, 0, allBytes, 1, 2)
        System.arraycopy(nameAndTypeIndex, 0, allBytes, 3, 2)

        return allBytes
    }
}

class ConstantInteger(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 3
    val integerBytes = ByteArray(4)
    var value = 0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        val localOffset = offset
        limit = localOffset + integerBytes.size
        for (ndx in localOffset..limit - 1) {
            integerBytes[ndx - localOffset] = classFile[ndx]
        }
        value = ByteBuffer.wrap(integerBytes).getInt()

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.ConstantValuePoolDisplayDetails(
            limit * 3,
            "Integer",
            getBytes(),
            integerBytes.toHex(),
            constantPoolIndex
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(5)

        allBytes[0] = 3

        System.arraycopy(integerBytes, 0, allBytes, 1, 4)
        return allBytes
    }
}

class ConstantFloat(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 4
    val floatBytes = ByteArray(4)
    var value = 0.0f
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        val localOffset = offset
        limit = localOffset + floatBytes.size
        for (ndx in localOffset..limit - 1) {
            floatBytes[ndx - localOffset] = classFile[ndx]
        }
        value = ByteBuffer.wrap(floatBytes).getFloat()

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.ConstantValuePoolDisplayDetails(
            limit * 3,
            "FLoat",
            getBytes(),
            floatBytes.toHex(),
            constantPoolIndex
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(5)

        allBytes[0] = 4

        System.arraycopy(allBytes, 0, allBytes, 1, 4)
        return allBytes
    }
}

class ConstantLong(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 5
    val highBytes = ByteArray(4)
    val lowBytes = ByteArray(4)
    val longBytes = ByteArray(8)
    var value: Long = 0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        var localOffset = offset
        limit = localOffset + highBytes.size
        for (ndx in localOffset..limit - 1) {
            highBytes[ndx - localOffset] = classFile[ndx]
        }

        localOffset = limit
        limit = localOffset + lowBytes.size

        for (ndx in localOffset..limit - 1) {
            lowBytes[ndx - localOffset] = classFile[ndx]
        }

        System.arraycopy(highBytes, 0, longBytes, 0, 4)
        System.arraycopy(lowBytes, 0, longBytes, 4, 4)

        value = ByteBuffer.wrap(longBytes).getLong()

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.ConstantValuePoolDisplayDetails(
            limit * 3,
            "Long",
            getBytes(),
            value.toString(),
            constantPoolIndex
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(9)

        allBytes[0] = 5

        System.arraycopy(highBytes, 0, allBytes, 1, 4)
        System.arraycopy(lowBytes, 0, allBytes, 5, 4)
        return allBytes
    }
}

class ConstantDouble(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 6
    val highBytes = ByteArray(4)
    val lowBytes = ByteArray(4)
    val doubleBytes = ByteArray(8)
    var value: Double = 0.0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        var localOffset = offset
        limit = localOffset + highBytes.size
        for (ndx in localOffset..limit - 1) {
            highBytes[ndx - localOffset] = classFile[ndx]
        }

        localOffset = limit
        limit = localOffset + lowBytes.size

        for (ndx in localOffset..limit - 1) {
            lowBytes[ndx - localOffset] = classFile[ndx]
        }

        System.arraycopy(highBytes, 0, doubleBytes, 0, 4)
        System.arraycopy(lowBytes, 0, doubleBytes, 4, 4)

        value = ByteBuffer.wrap(doubleBytes).getDouble()

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.ConstantValuePoolDisplayDetails(
            limit * 3,
            "Double",
            getBytes(),
            value.toString(),
            constantPoolIndex
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(9)

        allBytes[0] = 6

        System.arraycopy(highBytes, 0, allBytes, 1, 4)
        System.arraycopy(lowBytes, 0, allBytes, 5, 4)
        return allBytes
    }
}

class ConstantString(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 8
    val stringIndex = ByteArray(2)
    var stringIndexInt: Short = 0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        val localOffset = offset
        limit = localOffset + stringIndex.size
        for (ndx in localOffset..limit - 1) {
            stringIndex[ndx - localOffset] = classFile[ndx]
        }
        stringIndexInt = ByteBuffer.wrap(stringIndex).short

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.NamedConstantPoolDisplayDetails(
            limit * 3,
            "String",
            getBytes(),
            "#${constantPoolIndex}",
            constantPoolIndex,
            stringIndexInt
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(3)

        allBytes[0] = 8

        System.arraycopy(stringIndex, 0, allBytes, 1, 2)
        return allBytes
    }
}

class ConstantMethodHandle(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 15
    val referenceKind = ByteArray(1)
    val referenceIndex = ByteArray(2)
    var referenceIndexInt: Short = 0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        var localOffset = offset
        limit = localOffset + referenceKind.size
        for (ndx in localOffset..limit - 1) {
            referenceKind[ndx - localOffset] = classFile[ndx]
        }

        localOffset = limit
        limit = localOffset + referenceIndex.size

        for (ndx in localOffset..limit - 1) {
            referenceIndex[ndx - localOffset] = classFile[ndx]
        }
        referenceIndexInt = ByteBuffer.wrap(referenceIndex).short


        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.BasicConstantPoolDisplayDetails(
            limit * 3,
            "Method Handle",
            getBytes(),
            "#${constantPoolIndex}",
            constantPoolIndex
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(4)

        allBytes[0] = 15

        System.arraycopy(referenceIndex, 0, allBytes, 1, 1)
        System.arraycopy(referenceIndex, 0, allBytes, 2, 2)

        return allBytes
    }
}

class ConstantMethodType(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 16
    val descriptorIndex = ByteArray(2)
    var descriptorIndexInt: Short = 0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        val localOffset = offset
        limit = localOffset + descriptorIndex.size
        for (ndx in localOffset..limit - 1) {
            descriptorIndex[ndx - localOffset] = classFile[ndx]
        }
        descriptorIndexInt = ByteBuffer.wrap(descriptorIndex).short

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.NamedConstantPoolDisplayDetails(
            limit * 3,
            "Method Type",
            getBytes(),
            "#${constantPoolIndex}",
            constantPoolIndex,
            descriptorIndexInt
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(3)

        allBytes[0] = 16

        System.arraycopy(descriptorIndex, 0, allBytes, 1, 1)

        return allBytes
    }
}

open class ConstantDynamic(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 17
    val bootstrapMethodAttrIndex = ByteArray(2)
    var bootstrapMethodAttrIndexInt: Short = 0
    val nameAndTypeIndex = ByteArray(2)
    var nameAndTypeIndexInt: Short = 0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        var localOffset = offset
        limit = localOffset + bootstrapMethodAttrIndex.size
        for (ndx in localOffset..limit - 1) {
            bootstrapMethodAttrIndex[ndx - localOffset] = classFile[ndx]
        }

        bootstrapMethodAttrIndexInt = ByteBuffer.wrap(bootstrapMethodAttrIndex).short

        localOffset = limit
        limit = localOffset + nameAndTypeIndex.size

        for (ndx in localOffset..limit - 1) {
            nameAndTypeIndex[ndx - localOffset] = classFile[ndx]
        }

        nameAndTypeIndexInt = ByteBuffer.wrap(nameAndTypeIndex).short

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.BasicConstantPoolDisplayDetails(
            limit * 3,
            "ConstantDynamic",
            getBytes(),
            "#${constantPoolIndex}",
            constantPoolIndex
            )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(5)

        allBytes[0] = 17

        System.arraycopy(bootstrapMethodAttrIndex, 0, allBytes, 1, 2)
        System.arraycopy(nameAndTypeIndex, 0, allBytes, 3, 2)

        return allBytes
    }
}

class ConstantInvokeDynamic(constantPoolIndex: Short) : ConstantDynamic(constantPoolIndex) {
    override val constantPoolType = 18
    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.BasicConstantPoolDisplayDetails(
            limit * 3,
            "InvokeDynamic",
            getBytes(),
            "#${constantPoolIndex}",
            constantPoolIndex
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(5)

        allBytes[0] = 18

        System.arraycopy(bootstrapMethodAttrIndex, 0, allBytes, 1, 2)
        System.arraycopy(nameAndTypeIndex, 0, allBytes, 3, 2)

        return allBytes
    }
}

class ConstantPackage(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 20
    val nameIndex = ByteArray(2)
    var nameIndexInt: Short = 0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        val localOffset = offset
        limit = localOffset + nameIndex.size
        for (ndx in localOffset..limit - 1) {
            nameIndex[ndx - localOffset] = classFile[ndx]
        }
        nameIndexInt = ByteBuffer.wrap(nameIndex).short

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.NamedConstantPoolDisplayDetails(
            limit * 3,
            "Package",
            getBytes(),
            "#${constantPoolIndex}",
            constantPoolIndex,
            nameIndexInt
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(3)

        allBytes[0] = 20

        System.arraycopy(nameIndex, 0, allBytes, 1, 2)

        return allBytes
    }
}

class ConstantModule(constantPoolIndex: Short) : ConstantPool(constantPoolIndex) {
    override val constantPoolType = 19
    val nameIndex = ByteArray(2)
    var nameIndexInt: Short = 0
    var limit: Int = 0

    override fun parse(classFile: ByteArray, offset: Int): Int {
        val localOffset = offset
        limit = localOffset + nameIndex.size
        for (ndx in localOffset..limit - 1) {
            nameIndex[ndx - localOffset] = classFile[ndx]
        }
        nameIndexInt = ByteBuffer.wrap(nameIndex).short

        return limit
    }

    override fun getDisplayDetails(): ClassfileDisplayDetails {
        return ClassfileDisplayDetails.NamedConstantPoolDisplayDetails(
            limit * 3,
            "Package",
            getBytes(),
            "#${constantPoolIndex}",
            constantPoolIndex,
            nameIndexInt
        )
    }

    override fun getBytes(): ByteArray {
        val allBytes = ByteArray(3)

        allBytes[0] = 20

        System.arraycopy(nameIndex, 0, allBytes, 1, 2)

        return allBytes
    }
}

class FieldMethodInfo {
    val accessFlags = ByteArray(2)
    val nameIndex = ByteArray(2)
    var nameIndexInt: Short = 0
    val descriptorIndex = ByteArray(2)
    var descriptorIndexInt: Short = 0
    val attributeCount = ByteArray(2)
    var attribureCountInt: Short = 0

    val attributeInfo = mutableListOf<AttributeInfo>()

    fun parse(classFile: ByteArray, offset: Int): Int {
        var localOffset = offset
        var limit = localOffset + accessFlags.size
        for (ndx in localOffset..limit - 1) {
            accessFlags[ndx - localOffset] = classFile[ndx]
        }

        localOffset = limit
        limit = localOffset + nameIndex.size

        for (ndx in localOffset..limit - 1) {
            nameIndex[ndx - localOffset] = classFile[ndx]
        }
        nameIndexInt = ByteBuffer.wrap(nameIndex).short
        localOffset = limit
        limit = localOffset + descriptorIndex.size

        for (ndx in localOffset..limit - 1) {
            descriptorIndex[ndx - localOffset] = classFile[ndx]
        }
        descriptorIndexInt = ByteBuffer.wrap(descriptorIndex).short

        localOffset = limit
        limit = localOffset + attributeCount.size

        for (ndx in localOffset..limit - 1) {
            attributeCount[ndx - localOffset] = classFile[ndx]
        }
        attribureCountInt = ByteBuffer.wrap(attributeCount).short

        if (attribureCountInt > 0) {
            for (attributeNdx in 0..attribureCountInt - 1) {
                localOffset = limit
                val attrInfo = AttributeInfo()
                limit = attrInfo.parse(classFile, localOffset)
                attributeInfo.add(attrInfo)
            }
        }
        return limit
    }


    fun getBytes(): ByteArray {
        val allBytes = mutableListOf<Byte>()

        allBytes.addAll(accessFlags.toList())
        allBytes.addAll(nameIndex.toList())
        allBytes.addAll(descriptorIndex.toList())
        allBytes.addAll(attributeCount.toList())

        attributeInfo.forEach {
            val bytes = it.getBytes()
            allBytes.addAll(bytes.toList())
        }

        return allBytes.toByteArray()
    }
}

class AttributeInfo {
    val attributeNameIndex = ByteArray(2)
    var attributeNameIndexInt: Short = 0
    val attributeLength = ByteArray(4)
    var attributeLengthInt: Int = 0
    val info = mutableListOf<Byte>()

    fun parse(classFile: ByteArray, offset: Int): Int {
        var localOffset = offset
        var limit = localOffset + attributeNameIndex.size
        for (ndx in localOffset..limit - 1) {
            attributeNameIndex[ndx - localOffset] = classFile[ndx]
        }
        attributeNameIndexInt = ByteBuffer.wrap(attributeNameIndex).short

        localOffset = limit
        limit = localOffset + attributeLength.size

        for (ndx in localOffset..limit - 1) {
            attributeLength[ndx - localOffset] = classFile[ndx]
        }
        attributeLengthInt = ByteBuffer.wrap(attributeLength).getInt()

        localOffset = limit
        limit = localOffset + attributeLengthInt

        if (attributeLengthInt > 0) {
            for (dataNdx in localOffset..limit - 1) {
                info.add(classFile[dataNdx])
            }
        }

        return limit
    }


    fun getBytes(): ByteArray {
        val allBytes = mutableListOf<Byte>()
        allBytes.addAll(attributeNameIndex.toList())
        allBytes.addAll(attributeLength.toList())
        allBytes.addAll(info)

        return allBytes.toByteArray()
    }
}

fun getStringFromConstantPool(cfp: ClassFileParser, constantPoolIndex: Short): String {
    val poolEntry = cfp.constantPoolEntries.first {it.constantPoolIndex == constantPoolIndex}

    val constantPoolString = when (poolEntry) {
        is ConstantPackage -> {
            return getStringFromConstantPool(cfp, poolEntry.nameIndexInt)
        }
        is ConstantClass -> {
            return getStringFromConstantPool(cfp, poolEntry.nameIndexInt)
        }
        is ConstantDouble -> {
            return "double"
        }
        is ConstantDynamic -> {
            return getStringFromConstantPool(cfp, poolEntry.nameAndTypeIndexInt)
        }
        is ConstantFieldRef -> {
            return "${getStringFromConstantPool(cfp, poolEntry.classIndexInt)};${
                getStringFromConstantPool(
                    cfp,
                    poolEntry.nameAndTypeIndexInt
                )
            }"
        }
        is ConstantFloat -> {
            return "float"
        }
        is ConstantInteger -> {
            return "integer"
        }
        is ConstantInterfaceRef -> {
            return "${getStringFromConstantPool(cfp, poolEntry.classIndexInt)};${
                getStringFromConstantPool(
                    cfp,
                    poolEntry.nameAndTypeIndexInt
                )
            }"
        }
        is ConstantLong -> {
            return "long"
        }
        is ConstantMethodHandle -> {
            return "method handle"
        }
        is ConstantMethodRef -> {
            return "${getStringFromConstantPool(cfp, poolEntry.classIndexInt)};${
                getStringFromConstantPool(
                    cfp,
                    poolEntry.nameAndTypeIndexInt
                )
            }"
        }
        is ConstantMethodType -> {
            return getStringFromConstantPool(cfp, poolEntry.descriptorIndexInt)
        }
        is ConstantModule -> {
            return getStringFromConstantPool(cfp, poolEntry.nameIndexInt)
        }
        is ConstantNameAndType -> {
            return "${getStringFromConstantPool(cfp, poolEntry.nameIndexInt)};${
                getStringFromConstantPool(
                    cfp,
                    poolEntry.descriptorIndexInt
                )
            }"
        }
        is ConstantPoolUtf8 -> {
            poolEntry.strValue
        }
        is ConstantString -> {
            return getStringFromConstantPool(cfp, poolEntry.stringIndexInt)
        }
    }

    return constantPoolString
}
