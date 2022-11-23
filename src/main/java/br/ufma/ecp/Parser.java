package br.ufma.ecp;

import static br.ufma.ecp.token.TokenType.*;

import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;

public class Parser {

    private Scanner scan;
    private Token currentToken;
    private Token peekToken;

    private StringBuilder xmlOutput = new StringBuilder();

    public Parser(byte[] input) {
        scan = new Scanner(input);
        nextToken();
    }

    private void nextToken() {
        currentToken = peekToken;
        peekToken = scan.nextToken();
    }

    void parser() {
        parserClass();
    }

    // 'class' className '{' classVarDec* subroutineDec* '}'
    void parserClass() {
        printNonTerminal("class");
        expectPeek(CLASS);
        expectPeek(IDENTIFIER);
        expectPeek(LBRACE);
        while (peekToken.type == FIELD || peekToken.type == STATIC) {
            parserClassVarDec();
        }

        while (peekTokenIs(CONSTRUCTOR) ||
                peekTokenIs(FUNCTION) ||
                peekTokenIs(METHOD)) {
            parserSubroutineDec();
        }
        expectPeek(RBRACE);
        printNonTerminal("/class");
    }

    // ( 'static' | 'field' ) type varName ( ',' varName)* ';'
    void parserClassVarDec() {
        printNonTerminal("classVarDec");
        expectPeek(FIELD, STATIC);
        expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER);
        expectPeek(IDENTIFIER);
        while (peekToken.type == COMMA) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);
        }
        expectPeek(SEMICOLON);
        printNonTerminal("/classVarDec");
    }

    // subroutineDec - ( 'constructor' | 'function' | 'method' ) ( 'void' | type)
    // subroutineName '(' parameterList ')' subroutineBody

    void parserSubroutineDec() {
        printNonTerminal("subroutineDec");
        if (peekTokenIs(CONSTRUCTOR) ||
                peekTokenIs(FUNCTION) ||
                peekTokenIs(METHOD)) {
            expectPeek(peekToken.type);
            switch (peekToken.type) {
                case INT, CHAR, BOOLEAN, VOID:
                    expectPeek(peekToken.type);
                    break;
                case IDENTIFIER:
                    expectPeek(peekToken.type);
                    break;
                default:
                    ;
            }
        }
        expectPeek(IDENTIFIER);
        expectPeek(LPAREN);
        parserParameterList();
        expectPeek(RPAREN);
        parserSubroutineBody();
        printNonTerminal("/subroutineDec");
    }

    // parameterList - ((type varName) ( ',' type varName)*)?
    void parserParameterList() {
        printNonTerminal("parameterList");
        switch (peekToken.type) {
            case INT, CHAR, BOOLEAN, STRING:
                expectPeek(INT, CHAR, BOOLEAN, STRING);
                expectPeek(IDENTIFIER);
                while (peekTokenIs(COMMA)) {
                    expectPeek(COMMA);
                    expectPeek(INT, CHAR, BOOLEAN, STRING);
                    expectPeek(IDENTIFIER);
                }
                break;
            default:
                ;
        }
        printNonTerminal("/parameterList");
    }

    // SUBROUTIEN BODY - '{' varDec* statements '}'
    void parserSubroutineBody() {
        printNonTerminal("subroutineBody");
        expectPeek(LBRACE);
        while (peekTokenIs(VAR)) {
            parserVarDec();
        }
        parserStatements();
        expectPeek(RBRACE);
        printNonTerminal("/subroutineBody");
    }

    // 'var' type varName ( ',' varName)* ';'
    void parserVarDec() {
        printNonTerminal("varDec");
        expectPeek(VAR);
        expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER);
        expectPeek(IDENTIFIER);
        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);
        }
        expectPeek(SEMICOLON);
        printNonTerminal("/varDec");
    }

    // STATEMENTS - statement*
    void parserStatements() {
        printNonTerminal("statements");
        while (peekTokenIs(WHILE) ||
                peekTokenIs(IF) ||
                peekTokenIs(LET) ||
                peekTokenIs(DO) ||
                peekTokenIs(RETURN)) {
            parserStatement();
        }
        printNonTerminal("/statements");

    }

    // letStatement | ifStatement | whileStatement | doStatement | returnStatement
    void parserStatement() {
        switch (peekToken.type) {
            case LET:
                parserLet();
                break;
            case WHILE:
                parserWhile();
                break;
            case IF:
                parserIf();
                break;
            case DO:
                parserDo();

                break;
            case RETURN:
                parserReturn();
                break;
            default:
                ;
        }

    }

    // letStatement -> 'let' identifier( '[' expression ']' )? '=' expression ';’
    void parserLet() {
        printNonTerminal("letStatement");
        expectPeek(LET);
        expectPeek(IDENTIFIER);
        if (peekToken.type == LBRACKET) {
            expectPeek(LBRACKET);
            parserExpression();
            expectPeek(RBRACKET);
        }
        expectPeek(EQ);
        parserExpression();
        expectPeek(SEMICOLON);
        printNonTerminal("/letStatement");
        if (peekTokenIs(LET)) {
            parserLet();
        }

    }

    // 'if' '(' expression ')' '{' statements '}' ( 'else' '{' statements '}' )?
    void parserIf() {
        printNonTerminal("ifStatement");
        expectPeek(IF);
        expectPeek(LPAREN);
        parserExpression();
        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parserStatements();
        expectPeek(RBRACE);

        if (peekTokenIs(ELSE)) {
            expectPeek(ELSE);
            expectPeek(LBRACE);
            parserStatements();
            expectPeek(RBRACE);
        }
        printNonTerminal("/ifStatement");
    }

    // 'while' '(' expression ')' '{' statements '}'
    void parserWhile() {
        printNonTerminal("whileStatement");
        expectPeek(WHILE);
        expectPeek(LPAREN);
        parserExpression();
        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parserStatements();
        expectPeek(RBRACE);
        printNonTerminal("/whileStatement");
    }

    // 'do' subroutineCall ';'
    void parserDo() {
        printNonTerminal("doStatement");
        expectPeek(DO);
        expectPeek(IDENTIFIER);
        parserSubrotineCall();
        expectPeek(SEMICOLON);
        printNonTerminal("/doStatement");
    }

    // 'return' expression? ';'
    void parserReturn() {
        printNonTerminal("returnStatement");
        expectPeek(RETURN);
        if (!peekTokenIs(SEMICOLON)) {
            parserExpression();
        }
        expectPeek(SEMICOLON);
        printNonTerminal("/returnStatement");
    }

    // term (op term)*
    void parserExpression() {
        printNonTerminal("expression");
        parserTerm();
        while (isOperator(peekToken)) {
            expectPeek(peekToken.type);
            parserTerm();
        }
        printNonTerminal("/expression");
    }

    // integerConstant | stringConstant | keywordConstant | varName | varName '['
    // expression ']' | subroutineCall | '(' expression ')' | unaryOp term
    void parserTerm() {
        printNonTerminal("term");
        switch (peekToken.type) {
            case NUMBER:
                expectPeek(NUMBER);
                break;
            case STRING:
                expectPeek(STRING);
                break;
            case IDENTIFIER:
                expectPeek(IDENTIFIER);
                if (peekTokenIs(LPAREN) || peekTokenIs(DOT)) {
                    parserSubrotineCall();
                } else {
                    if (peekTokenIs(LBRACKET)) {
                        expectPeek(LBRACKET);
                        parserExpression();
                        expectPeek(RBRACKET);
                    }
                }
                break;

            case TRUE, FALSE, NULL:
                expectPeek(TRUE, FALSE, NULL);
                break;
            case THIS:
                expectPeek(THIS);
                break;
            case MINUS, NOT:
                expectPeek(MINUS, NOT);
                parserTerm();
                break;
            case LPAREN:
                expectPeek(LPAREN);
                parserExpression();
                expectPeek(RPAREN);
                break;
            default:
                ;

        }
        printNonTerminal("/term");
    }

    // subroutineName '(' expressionList ')' | (className|varName)'.' subroutineName
    // '(' expressionList ')'
    void parserSubrotineCall() {
        if (peekTokenIs(LPAREN)) {
            expectPeek(LPAREN);
            parserExpressionList();
            expectPeek(RPAREN);

        } else {
            expectPeek(DOT);
            expectPeek(IDENTIFIER);
            expectPeek(LPAREN);
            parserExpressionList();
            expectPeek(RPAREN);
        }
    }

    // (expression ( ',' expression)* )?
    void parserExpressionList() {
        printNonTerminal("expressionList");
        if (!peekTokenIs(RPAREN)) {
            parserExpression();
        }
        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            parserExpression();
        }
        printNonTerminal("/expressionList");
    }

    // auxiliares

    boolean currentTokenIs(TokenType type) {
        return currentToken.type == type;
    }

    boolean peekTokenIs(TokenType type) {
        return peekToken.type == type;
    }

    private void expectPeek(TokenType type) {
        if (peekToken.type == type) {
            System.out.println(peekToken);
            nextToken();
            xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
        } else {
            throw new Error("Syntax error - expected " + type + " found " + peekToken.lexeme);
        }
    }

    private void expectPeek(TokenType... types) {

        for (TokenType type : types) {
            if (peekToken.type == type) {
                expectPeek(type);
                return;
            }
        }

        throw new Error("Syntax error ");

    }

    public String XMLOutput() {
        return xmlOutput.toString();
    }

    private void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
    }

    boolean isOperator(Token c) {
        String isSymbol = "+-*/&|><=~";
        return (isSymbol.contains(c.lexeme));
    }

}
