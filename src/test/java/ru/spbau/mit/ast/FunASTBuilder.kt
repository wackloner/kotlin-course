package ru.spbau.mit.ast

import org.antlr.v4.runtime.ParserRuleContext
import ru.spbau.mit.FunBaseVisitor
import ru.spbau.mit.FunParser
import ru.spbau.mit.ast.FunAST.*

class FunASTBuilder : FunBaseVisitor<FunAST.Node>() {
    fun build(ctx: ParserRuleContext): FunAST {
        val rootNode = visit(ctx)
        return FunAST(rootNode)
    }

    override fun visitBlock(ctx: FunParser.BlockContext?) = Block(ctx!!.statement().map { visit(it) as Statement })

    override fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext?) =
            BracedBlock(visit(ctx!!.block()) as Block)

    override fun visitParameterNames(ctx: FunParser.ParameterNamesContext?) =
            ParameterNames(ctx!!.Identifier().map { it.text })

    override fun visitFunction(ctx: FunParser.FunctionContext?) =
            FunctionDeclaration(
                ctx!!.Identifier().text,
                visit(ctx.parameterNames()) as ParameterNames,
                getBrBlock(ctx.blockWithBraces())
            )

    override fun visitVariable(ctx: FunParser.VariableContext?) =
            Variable(ctx!!.Identifier().text, getExpr(ctx.expression()))

    override fun visitWhileStatement(ctx: FunParser.WhileStatementContext?) =
            While(
                getExpr(ctx!!.expression()),
                getBrBlock(ctx.blockWithBraces())
            )

    override fun visitIfStatement(ctx: FunParser.IfStatementContext?) =
            If(
                getExpr(ctx!!.expression()),
                getBrBlock(ctx.blockWithBraces(0)),
                if (ctx.blockWithBraces().size > 1) getBrBlock(ctx.blockWithBraces(1)) else null
            )

    override fun visitAssignment(ctx: FunParser.AssignmentContext?) =
            Assignment(
                ctx!!.Identifier().text,
                getExpr(ctx.expression())
            )

    override fun visitReturnStatement(ctx: FunParser.ReturnStatementContext?) = Return(getExpr(ctx!!.expression()))

    override fun visitIdentifierFunctionCall(ctx: FunParser.IdentifierFunctionCallContext?) =
            IdentifierFunctionCall(
                ctx!!.Identifier().text,
                getArgs(ctx.arguments())
            )

    override fun visitPrintlnFunctionCall(ctx: FunParser.PrintlnFunctionCallContext?) =
            PrintFunctionCall(getArgs(ctx!!.arguments()))

    override fun visitIdentifierExpression(ctx: FunParser.IdentifierExpressionContext?) =
            Identifier(ctx!!.Identifier().text)

    override fun visitLiteralExpression(ctx: FunParser.LiteralExpressionContext?) = Literal(ctx!!.text.toInt())

    override fun visitExpressionWithParentheses(ctx: FunParser.ExpressionWithParenthesesContext?) =
            BracedExpression(getExpr(ctx!!.expression()))

    override fun visitUnaryMinusExpression(ctx: FunParser.UnaryMinusExpressionContext?) =
            UnaryMinus(getExpr(ctx!!.expression()))

    override fun visitBinaryOperationExpression(ctx: FunParser.BinaryOperationExpressionContext?) =
            BinaryOperation(
                getExpr(ctx!!.expression(0)),
                ctx.op.text,
                getExpr(ctx.expression(1))
            )

    override fun visitArguments(ctx: FunParser.ArgumentsContext?) =
            Arguments(ctx!!.expression().map { visit(it) as Expression })

    private fun getExpr(ctx: FunParser.ExpressionContext) = visit(ctx) as Expression

    private fun getBrBlock(ctx: FunParser.BlockWithBracesContext) = visit(ctx) as BracedBlock

    private fun getArgs(ctx: FunParser.ArgumentsContext) = visit(ctx) as Arguments
}