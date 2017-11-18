package ru.spbau.mit.tex

interface Element {
    fun render(builder: StringBuilder, indent: String)
}

class TextElement(val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

abstract class Tag(val name: String) : Element {
    val attributes = hashMapOf<String, String>()
}

abstract class TagWithContent(name: String) : Tag(name) {
    val content = mutableListOf<Element>()
}
