package ru.spbau.mit

import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.tree.ParseTree
import javax.swing.JFrame
import org.antlr.v4.gui.TreeViewer
import javax.swing.JPanel




fun main(args: Array<String>) {
    val lexer = FunLexer(ANTLRFileStream("src/main/examples/first.fun"))
    val parser = FunParser(CommonTokenStream(lexer))
    parser.buildParseTree = true
    val tree = parser.file()
//    showTree(tree, parser)

    val visitor = InterpretationVisitor()
    try {
        visitor.visit(tree)
    } catch (exception: FunException) {
        print("Exception: " + exception.message)
    }
}

fun showTree(tree: ParseTree, parser: Parser) {
    val frame = JFrame("Antlr AST")
    val panel = JPanel()
    val viewer = TreeViewer(parser.ruleNames.asList(), tree)
    viewer.scale = 1.5
    panel.add(viewer)
    frame.add(panel)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(1000, 1000)
    frame.isVisible = true
}
