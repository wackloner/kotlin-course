package ru.spbau.mit.tex

class TexException(message: String) : Throwable(message)

abstract class Element {
    abstract fun render(builder: StringBuilder, indent: String)

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

class TextElement(private val text: String) : Element() {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

abstract class Tag(val name: String) : Element() {
    private val parameters = mutableListOf<String>()
    protected open val arguments: String? = null

    protected open fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        return tag
    }

    protected fun renderParameters(): String =
            if (parameters.isEmpty()) "" else parameters.joinToString(" ,", "[", "]")

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("\\$name${renderParameters()}")
        if (arguments != null) {
            builder.append("{$arguments}")
        }
        builder.append('\n')
    }
}

abstract class TagWithContent(name: String) : Tag(name) {
    private val content = mutableListOf<Element>()

    override fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        content.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin{$name}${renderParameters()}\n")
        content.forEach { it.render(builder, indent + "  ") }
        builder.append("$indent\\end{$name}\n")
    }
}

class DocumentClass(private val type: String) : Tag("documentclass") {
    override val arguments: String
        get() = type
}

class UsePackage(private val packages: Array<out String>) : Tag("usepackage") {
    override val arguments
        get() = packages.joinToString()
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

    fun documentClass(type: String) {
        documentClass = DocumentClass(type)
    }

    fun usePackage(vararg packages: String) = usePackages.add(UsePackage(packages))
}

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}
