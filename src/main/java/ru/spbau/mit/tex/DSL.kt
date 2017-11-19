package ru.spbau.mit.tex

import java.io.OutputStream

@DslMarker
annotation class TexElementMarker

@TexElementMarker
abstract class Element(
        private val additionalArgs: Array<out String>? = null
) {
    abstract fun render(builder: StringBuilder, indent: String)

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }

    fun renderArgs(): String =
        if (additionalArgs == null || additionalArgs.isEmpty())
            ""
        else
            additionalArgs.joinToString(" ,", "[", "]")

    fun toOutputStream(stream: OutputStream = System.out) = stream.write(toString().toByteArray())
}

class TextElement(private val text: String) : Element() {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

class Math(private val text: String) : Element() {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("\$$text\$\n")
    }
}

abstract class Tag(
        private val name: String,
        additionalArgs: Array<out String>?
) : Element(additionalArgs) {
    protected val children = mutableListOf<Element>()

    fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin{$name}${renderArgs()}\n")
        children.forEach { it.render(builder, indent + "  ") }
        builder.append("$indent\\end{$name}\n")
    }

    operator fun String.unaryPlus() = children.add(TextElement(this))
}

abstract class TagWithItems(
        name: String,
        additionalArgs: Array<out String>?
) : Tag(name, additionalArgs) {
    fun item(vararg additionalArgs: String, init: Item.() -> Unit) = initTag(Item(additionalArgs), init)
}

class Itemize(additionalArgs: Array<out String>?) : TagWithItems("itemize", additionalArgs)

class Enumerate(additionalArgs: Array<out String>?) : TagWithItems("enumerate", additionalArgs)

abstract class TagWithContent(
        name: String,
        additionalArgs: Array<out String>? = null
) : Tag(name, additionalArgs) {
    fun math(text: String) = initTag(Math(text), {})

    fun itemize(vararg additionalArgs: String, init: Itemize.() -> Unit) = initTag(Itemize(additionalArgs), init)

    fun enumerate(vararg additionalArgs: String, init: Enumerate.() -> Unit) = initTag(Enumerate(additionalArgs), init)

    fun left(init: Left.() -> Unit) = initTag(Left(), init)

    fun center(init: Center.() -> Unit) = initTag(Center(), init)

    fun right(init: Right.() -> Unit) = initTag(Right(), init)

    fun customTag(name: String, vararg additionalArgs: String, init: CustomTag.() -> Unit) =
            initTag(CustomTag(name, additionalArgs), init)

    fun frame(frameTitle: String, vararg additionalArgs: String, init: Frame.() -> Unit) =
            initTag(Frame(frameTitle, additionalArgs), init)
}

class Item(additionalArgs: Array<out String>?) : TagWithContent("item", additionalArgs) {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\item${renderArgs()}\n")
        children.forEach { it.render(builder, indent + "  ") }
    }
}

abstract class Alignment(type: String) : TagWithContent(type)

class Left : Alignment("left")

class Center : Alignment("center")

class Right : Alignment("right")

class CustomTag(
        name: String,
        additionalArgs: Array<out String>?
) : TagWithContent(name, additionalArgs)

class Frame(
        frameTitle: String,
        additionalArgs: Array<out String>?
) : TagWithContent("frame", additionalArgs) {
    init {
        children.add(FrameTitle(frameTitle))
    }
}

class Document : TagWithContent("document") {
    private var documentClass: DocumentClass? = null
        set(value) {
            if (field != null) {
                throw TexException("More than one documentClass!")
            }
            field = value
        }

    private val usePackages = mutableListOf<UsePackage>()

    override fun render(builder: StringBuilder, indent: String) {
        documentClass?.render(builder, indent) ?: throw TexException("No documentClass specified!")
        usePackages.forEach { it.render(builder, indent)}
        super.render(builder, indent)
    }

    fun documentClass(arg: String, vararg additionalArgs: String) {
        documentClass = DocumentClass(arg, additionalArgs)
    }

    fun usePackage(arg: String, vararg additionalArgs: String) = usePackages.add(UsePackage(arg, additionalArgs))
}

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}

abstract class Command(
        private val name: String,
        private val arg: String,
        additionalArgs: Array<out String>?
) : Element(additionalArgs) {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\$name${renderArgs()}{$arg}\n")
    }
}

class DocumentClass(
        arg: String,
        additionalArgs: Array<out String>?
) : Command("documentclass", arg, additionalArgs)

class UsePackage(
        arg: String,
        additionalArgs: Array<out String>?
) : Command("usepackage", arg, additionalArgs)

class FrameTitle(
        arg: String,
        additionalArgs: Array<out String>? = null
) : Command("frametitle", arg, additionalArgs)

class TexException(message: String) : Throwable(message)