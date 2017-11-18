package ru.spbau.mit

import ru.spbau.mit.tex.*


fun main(args: Array<String>) {
    val doc = document {
        documentClass("beamer")
        usePackage("ololo", "heh")
    }
    print(doc)
}
