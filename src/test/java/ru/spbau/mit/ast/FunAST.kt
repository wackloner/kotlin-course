package ru.spbau.mit.ast

data class FunAST(val rootNode: Node) {
    interface Node

    interface Statement : Node

    interface Expression : Statement

    data class Literal(val value: Int) : Expression

    data class Identifier(val name: String) : Expression

    data class BinaryOperation(
            val lhs: Expression,
            val op: String,
            val rhs: Expression
    ) : Expression

    data class Arguments(val expressions: List<Expression>) : Node

    data class PrintFunctionCall(val arguments: Arguments) : Expression

    data class IdentifierFunctionCall(
            val identifier: String,
            val arguments: Arguments
    ) : Expression

    data class UnaryMinus(val expression: Expression) : Expression

    data class BracedExpression(val expression: Expression) : Expression

    data class Return(val expression: Expression) : Statement

    data class Assignment(
            val identifier: String,
            val expression: Expression
    ) : Statement

    data class If(
            val condition: Expression,
            val body: BracedBlock,
            val elseBody: BracedBlock? = null
    ) : Statement

    data class While(
            val condition: Expression,
            val body: BracedBlock
    ) : Statement

    data class ParameterNames(val names: List<String>) : Node

    data class Variable(
            val name: String,
            val value: Expression
    ) : Statement

    data class FunctionDeclaration(
            val name: String,
            val parameterNames: ParameterNames,
            val body: BracedBlock
    ) : Statement

    data class Block(val statements: List<Statement>) : Node

    data class BracedBlock(val block: Block) : Node
}