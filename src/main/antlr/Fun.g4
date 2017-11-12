grammar Fun;

block
    : statement*
    ;

blockWithBraces
    : '{' block '}'
    ;

statement
    : function
    | variable
    | expression
    | whileStatement
    | ifStatement
    | assignment
    | returnStatement
    ;

function
    : 'fun' Identifier '(' parameterNames ')' blockWithBraces
    ;

variable
    : 'var' Identifier ('=' expression)?
    ;

parameterNames
    : Identifier (',' Identifier)*
    ;

whileStatement
    : 'while' '(' expression ')' blockWithBraces
    ;

ifStatement
    : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
    ;

assignment
    : Identifier '=' expression
    ;

returnStatement
    : 'return' expression
    ;

// split binaryOperation to several blocks for good precedence parsing
expression
    : functionCall                                           #functionCallExpression
    | Identifier                                             #identifierExpression
    | Literal                                                #literalExpression
    | '(' expression ')'                                     #expressionWithParentheses
    | '-' expression                                         #unaryMinusExpression
    | expression op=('*'|'/'|'%') expression                 #binaryOperationExpression
    | expression op=('+'|'-') expression                     #binaryOperationExpression
    | expression op=('>'|'<'|'>='|'<='|'=='|'!=') expression #binaryOperationExpression
    | expression op='&&' expression                          #binaryOperationExpression
    | expression op='||' expression                          #binaryOperationExpression
    ;

functionCall
    : Identifier '(' arguments ')' #identifierFunctionCall
    | Println '(' arguments ')'    #printlnFunctionCall
    ;

arguments
    : expression (',' expression)*
    ;

Println
    : 'println'
    ;

Identifier
    : [a-zA-Z_] [a-zA-Z_0-9]*
    ;

Literal
    : [1-9] [0-9]*
    | '0'
    ;

Comment
    : '//' ~[\r\n]* -> skip
    ;

Space
    : [ \t\r\n] -> skip
    ;


