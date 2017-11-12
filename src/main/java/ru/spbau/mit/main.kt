package ru.spbau.mit

import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.tree.ParseTree
import javax.swing.JFrame
import org.antlr.v4.gui.TreeViewer
import javax.swing.JPanel


fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Error: No file specified for parsing.")
        return
    }

    val tree = ParseTreeBuilder.build(args[0])
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
