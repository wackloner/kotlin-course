package ru.spbau.mit

import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.tree.ParseTree
import javax.swing.JFrame
import org.antlr.v4.gui.TreeViewer
import ru.spbau.mit.interpreter.FunException
import ru.spbau.mit.interpreter.InterpretationVisitor
import javax.swing.JPanel


fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Error: No file specified for parsing.")
        return
    }

    val parseTree = ParseTreeBuilder.build(args[0])

    val visitor = InterpretationVisitor()
    try {
        visitor.visit(parseTree)
    } catch (exception: IllegalStateException) {
        System.err.println("Parsing exception: " + exception.message)
    } catch (exception: FunException) {
        System.err.println("Interpretation exception: " + exception.message)
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
