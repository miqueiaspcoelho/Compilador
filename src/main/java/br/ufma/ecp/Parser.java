package br.ufma.ecp;

import static br.ufma.ecp.token.TokenType.*;

import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;
import br.ufma.ecp.SymbolTable.Kind;
import br.ufma.ecp.SymbolTable.Symbol;
import br.ufma.ecp.VMWriter.Command;
import br.ufma.ecp.VMWriter.Segment;

public class Parser {

    // adicionando erro
    private static class ParseError extends RuntimeException {
    }

    private Scanner scan;
    private Token currentToken;
    private Token peekToken;

    private StringBuilder xmlOutput = new StringBuilder();// output

    private SymbolTable symbolTable;// tabela de símbolos
    private VMWriter vmWriter;// vmwriter
    private String className;// usada para sabermos o nome da classe
    private int ifLabelNum;// números de ifs dentro de um escopo
    private int whileLabelNum;// número de whiles dentro de um escopo

    public Parser(byte[] input) {
        scan = new Scanner(input);
        symbolTable = new SymbolTable();
        vmWriter = new VMWriter();
        nextToken();
        ifLabelNum = 0;
        whileLabelNum = 0;

    }

    private void nextToken() {
        currentToken = peekToken;
        peekToken = scan.nextToken();
    }

    void parser() {
        parserClass();
    }

    // 'class' className '{' classVarDec* subroutineDec* '}'
    // ok
    void parserClass() {
        printNonTerminal("class");
        expectPeek(CLASS);
        expectPeek(IDENTIFIER);

        // adiciona o nome da classe
        className = currentToken.value();
        // symbolTable.resolve(className);

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
    // ok
    void parserClassVarDec() {
        printNonTerminal("classVarDec");
        expectPeek(FIELD, STATIC);

        SymbolTable.Kind kind = Kind.STATIC;
        if (currentTokenIs(FIELD))
            kind = Kind.FIELD; // tipo

        expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER);
        String type = currentToken.value();// valor

        expectPeek(IDENTIFIER);
        String name = currentToken.value();// nome

        symbolTable.define(name, type, kind);
        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);

            name = currentToken.value();
            symbolTable.define(name, type, kind);
        }
        expectPeek(SEMICOLON);
        printNonTerminal("/classVarDec");
    }

    // subroutineDec - ( 'constructor' | 'function' | 'method' ) ( 'void' | type)
    // subroutineName '(' parameterList ')' subroutineBody
    // sofreu uma quantidade considerável de alterações - ok
    void parserSubroutineDec() {
        printNonTerminal("subroutineDec");

        ifLabelNum = 0;
        whileLabelNum = 0;

        symbolTable.startSubroutine();

        expectPeek(CONSTRUCTOR, FUNCTION, METHOD);
        var subroutineType = currentToken.type;

        if (subroutineType == METHOD) {
            symbolTable.define("this", className, Kind.ARG);
        }

        // 'int' | 'char' | 'boolean' | className
        expectPeek(VOID, INT, CHAR, BOOLEAN, IDENTIFIER);
        expectPeek(IDENTIFIER);

        var functionName = className + "." + currentToken.value();

        expectPeek(LPAREN);
        parserParameterList();
        expectPeek(RPAREN);
        parserSubroutineBody(functionName, subroutineType);

        printNonTerminal("/subroutineDec");
    }

    // parameterList - ((type varName) ( ',' type varName)*)?
    // ok
    void parserParameterList() {
        printNonTerminal("parameterList");

        SymbolTable.Kind kind = Kind.ARG;

        if (!peekTokenIs(RPAREN)) // verifica se tem pelo menos uma expressao
        {
            expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER);
            String type = currentToken.value();

            expectPeek(IDENTIFIER);
            String name = currentToken.value();
            symbolTable.define(name, type, kind);

            while (peekTokenIs(COMMA)) {
                expectPeek(COMMA);
                expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER);
                type = currentToken.value();

                expectPeek(IDENTIFIER);
                name = currentToken.value();

                symbolTable.define(name, type, kind);
            }

        }

        printNonTerminal("/parameterList");

    }

    // SUBROUTINE BODY - '{' varDec* statements '}'
    // ok
    void parserSubroutineBody(String functionName, TokenType subroutineType) {
        printNonTerminal("subroutineBody");
        expectPeek(LBRACE);
        while (peekTokenIs(VAR)) {
            parserVarDec();
        }
        var nlocals = symbolTable.varCount(Kind.VAR);

        vmWriter.writeFunction(functionName, nlocals);

        if (subroutineType == CONSTRUCTOR) {
            vmWriter.writePush(Segment.CONST, symbolTable.varCount(Kind.FIELD));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(Segment.POINTER, 0);
        }

        if (subroutineType == METHOD) {
            vmWriter.writePush(Segment.ARG, 0);
            vmWriter.writePop(Segment.POINTER, 0);
        }

        parserStatements();
        expectPeek(RBRACE);
        printNonTerminal("/subroutineBody");

    }

    // 'var' type varName ( ',' varName)* ';'
    // ok
    void parserVarDec() {
        printNonTerminal("varDec");
        expectPeek(VAR);

        SymbolTable.Kind kind = Kind.VAR;

        expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER);
        String type = currentToken.value();

        expectPeek(IDENTIFIER);
        String name = currentToken.value();
        symbolTable.define(name, type, kind);

        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);
            name = currentToken.value();
            symbolTable.define(name, type, kind);
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
                throw error(peekToken, "Expected a statement");
        }

    }

    // letStatement -> 'let' identifier( '[' expression ']' )? '=' expression ';’
    // muitas modificações - ok.
    void parserLet() {
        var isArray = false;

        printNonTerminal("letStatement");
        expectPeek(LET);
        expectPeek(IDENTIFIER);

        // SymbolTable.Kind kind = Kind.STATIC;// não tenho certeza de qual kind deve
        // ser atribuído a um let normal
        // String type = currentToken.value();
        // String name = currentToken.value();
        // symbolTable.define(name, type, kind);

        var symbol = symbolTable.resolve(currentToken.value());

        if (peekTokenIs(LBRACKET)) { // array
            expectPeek(LBRACKET);
            parserExpression();

            vmWriter.writePush(kind2Segment(symbol.kind()), symbol.index());
            vmWriter.writeArithmetic(Command.ADD);

            expectPeek(RBRACKET);

            isArray = true;
        }

        expectPeek(EQ);
        parserExpression();

        if (isArray) {

            vmWriter.writePop(Segment.TEMP, 0); // push result back onto stack
            vmWriter.writePop(Segment.POINTER, 1); // pop address pointer into pointer 1
            vmWriter.writePush(Segment.TEMP, 0); // push result back onto stack
            vmWriter.writePop(Segment.THAT, 0); // Store right hand side evaluation in THAT 0.

        } else {
            if (symbol != null) {
                vmWriter.writePop(kind2Segment(symbol.kind()), symbol.index());
            } // fiz essa gambiarra, ver com o professor como resolver de maneira correta

        }

        expectPeek(SEMICOLON);
        printNonTerminal("/letStatement");

    }

    // 'if' '(' expression ')' '{' statements '}' ( 'else' '{' statements '}' )?
    // algumas modificações - ok
    void parserIf() {
        printNonTerminal("ifStatement");

        var labelTrue = "IF_TRUE" + ifLabelNum;
        var labelFalse = "IF_FALSE" + ifLabelNum;
        var labelEnd = "IF_END" + ifLabelNum;

        ifLabelNum++;

        expectPeek(IF);
        expectPeek(LPAREN);
        parserExpression();
        expectPeek(RPAREN);

        vmWriter.writeIf(labelTrue);
        vmWriter.writeGoto(labelFalse);
        vmWriter.writeLabel(labelTrue);

        expectPeek(LBRACE);
        parserStatements();
        expectPeek(RBRACE);

        if (peekTokenIs(ELSE)) {
            vmWriter.writeGoto(labelEnd);
        }

        vmWriter.writeLabel(labelFalse);

        if (peekTokenIs(ELSE)) {
            expectPeek(ELSE);

            expectPeek(LBRACE);

            parserStatements();

            expectPeek(RBRACE);
            vmWriter.writeLabel(labelEnd);
        }

        printNonTerminal("/ifStatement");
    }

    // 'while' '(' expression ')' '{' statements '}'
    // algumas modificações - ok
    void parserWhile() {
        printNonTerminal("whileStatement");

        var labelTrue = "WHILE_EXP" + whileLabelNum;
        var labelFalse = "WHILE_END" + whileLabelNum;
        whileLabelNum++;

        vmWriter.writeLabel(labelTrue);

        expectPeek(WHILE);
        expectPeek(LPAREN);
        parserExpression();

        vmWriter.writeArithmetic(Command.NOT);
        vmWriter.writeIf(labelFalse);

        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parserStatements();

        vmWriter.writeGoto(labelTrue); // Go back to labelTrue and check condition
        vmWriter.writeLabel(labelFalse); // Breaks out of while loop because ~(condition) is true

        expectPeek(RBRACE);
        printNonTerminal("/whileStatement");

    }

    // 'do' subroutineCall ';'
    // ok - uma única mudança
    void parserDo() {
        printNonTerminal("doStatement");
        expectPeek(DO);
        expectPeek(IDENTIFIER);
        parserSubrotineCall();
        expectPeek(SEMICOLON);
        vmWriter.writePop(Segment.TEMP, 0);
        printNonTerminal("/doStatement");
    }

    // 'return' expression? ';'
    // duas modificações - ok
    void parserReturn() {
        printNonTerminal("returnStatement");
        expectPeek(RETURN);
        if (!peekTokenIs(SEMICOLON)) {
            parserExpression();
        } else {
            vmWriter.writePush(Segment.CONST, 0);
        }
        expectPeek(SEMICOLON);
        vmWriter.writeReturn();
        printNonTerminal("/returnStatement");
    }

    // term (op term)*
    // duas modificações - ok
    void parserExpression() {
        printNonTerminal("expression");
        parserTerm();
        while (isOperator(peekToken.type)) {
            var op = peekToken.type;
            expectPeek(peekToken.type);
            parserTerm();
            compileOperators(op);
        }
        printNonTerminal("/expression");
    }

    // integerConstant | stringConstant | keywordConstant | varName | varName '['
    // expression ']' | subroutineCall | '(' expression ')' | unaryOp term
    // muitas modificações - ok
    void parserTerm() {
        printNonTerminal("term");
        switch (peekToken.type) {
            case INTEGER:
                expectPeek(INTEGER);
                vmWriter.writePush(Segment.CONST, Integer.parseInt(currentToken.value()));
                break;
            case STRING:
                expectPeek(STRING);
                var strValue = currentToken.value();
                vmWriter.writePush(Segment.CONST, strValue.length());
                vmWriter.writeCall("String.new", 1);
                for (int i = 0; i < strValue.length(); i++) {
                    vmWriter.writePush(Segment.CONST, strValue.charAt(i));
                    vmWriter.writeCall("String.appendChar", 2);
                }
                break;
            case FALSE:
            case NULL:
            case TRUE:
                expectPeek(FALSE, NULL, TRUE);
                vmWriter.writePush(Segment.CONST, 0);
                if (currentToken.type == TRUE)
                    vmWriter.writeArithmetic(Command.NOT);
                break;
            case THIS:
                expectPeek(THIS);
                vmWriter.writePush(Segment.POINTER, 0);
                break;
            case IDENTIFIER:
                expectPeek(IDENTIFIER);
                Symbol sym = symbolTable.resolve(currentToken.value());

                if (peekTokenIs(LPAREN) || peekTokenIs(DOT)) {
                    parserSubrotineCall();
                } else { // variavel comum ou array
                    if (peekTokenIs(LBRACKET)) { // array
                        expectPeek(LBRACKET);
                        parserExpression();
                        vmWriter.writePush(kind2Segment(sym.kind()), sym.index());
                        vmWriter.writeArithmetic(Command.ADD);

                        expectPeek(RBRACKET);
                        vmWriter.writePop(Segment.POINTER, 1); // pop address pointer into pointer 1
                        vmWriter.writePush(Segment.THAT, 0); // push the value of the address pointer back onto stack

                    } else {
                        if (sym != null) {// gambiarra de novo, não sei o motivo do sym está sendo null
                            vmWriter.writePush(kind2Segment(sym.kind()), sym.index());
                        }

                    }
                }
                break;
            case LPAREN:
                expectPeek(LPAREN);
                parserExpression();
                expectPeek(RPAREN);
                break;
            case MINUS:
            case NOT:
                expectPeek(MINUS, NOT);
                var op = currentToken.type;
                parserTerm();
                if (op == MINUS)
                    vmWriter.writeArithmetic(Command.NEG);
                else
                    vmWriter.writeArithmetic(Command.NOT);

                break;
            default:
                throw error(peekToken, "term expected");
        }
        printNonTerminal("/term");

    }

    // subroutineName '(' expressionList ')' | (className|varName)'.' subroutineName
    // '(' expressionList ')'
    // muitas modificações - ok
    void parserSubrotineCall() {
        var nArgs = 0;

        var ident = currentToken.value();
        var symbol = symbolTable.resolve(ident); // classe ou objeto
        var functionName = ident + ".";

        if (peekTokenIs(LPAREN)) { // método da propria classe
            expectPeek(LPAREN);
            vmWriter.writePush(Segment.POINTER, 0);
            nArgs = parserExpressionList() + 1;
            expectPeek(RPAREN);
            functionName = className + "." + ident;
        } else {
            // pode ser um metodo de um outro objeto ou uma função
            expectPeek(DOT);
            expectPeek(IDENTIFIER); // nome da função

            if (symbol != null) { // é um metodo
                functionName = symbol.type() + "." + currentToken.value();
                vmWriter.writePush(kind2Segment(symbol.kind()), symbol.index());
                nArgs = 1; // do proprio objeto
            } else {
                functionName += currentToken.value(); // é uma função
            }

            expectPeek(LPAREN);
            nArgs += parserExpressionList();

            expectPeek(RPAREN);
        }

        vmWriter.writeCall(functionName, nArgs);

    }

    // (expression ( ',' expression)* )?
    // poucas modificações - ok
    int parserExpressionList() {
        printNonTerminal("expressionList");
        var nArgs = 0;

        if (!peekTokenIs(RPAREN)) {
            parserExpression();
            nArgs = 1;
        }
        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            parserExpression();
            nArgs++;
        }
        printNonTerminal("/expressionList");
        return nArgs;
    }

    void compileOperators(TokenType type) {

        if (type == ASTERISK) {
            vmWriter.writeCall("Math.multiply", 2);
        } else if (type == SLASH) {
            vmWriter.writeCall("Math.divide", 2);
        } else {
            vmWriter.writeArithmetic(typeOperator(type));
        }
    }

    // auxiliares

    // FUNÇÕES AUXILIARES
    public String XMLOutput() {
        return xmlOutput.toString();
    }

    public String VMOutput() {
        return vmWriter.vmOutput();
    }

    private void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
    }

    boolean peekTokenIs(TokenType type) {
        return peekToken.type == type;
    }

    boolean currentTokenIs(TokenType type) {
        return currentToken.type == type;
    }

    private void expectPeek(TokenType... types) {
        for (TokenType type : types) {
            if (peekToken.type == type) {
                expectPeek(type);
                return;
            }
        }

        // throw new Error("Syntax error");
        throw error(peekToken, "Expected a statement");

    }

    private void expectPeek(TokenType type) {
        if (peekToken.type == type) {
            nextToken();
            xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
        } else {
            // throw new Error("Syntax error - expected " + type + " found " +
            // peekToken.type);
            throw error(peekToken, "Expected " + type.value);
        }
    }

    private static void report(int line, String where,
            String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
    }

    private ParseError error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.value() + "'", message);
        }
        return new ParseError();
    }

    private Segment kind2Segment(Kind kind) {
        if (kind == Kind.STATIC)
            return Segment.STATIC;
        if (kind == Kind.FIELD)
            return Segment.THIS;
        if (kind == Kind.VAR)
            return Segment.LOCAL;
        if (kind == Kind.ARG)
            return Segment.ARG;
        return null;
    }

    private Command typeOperator(TokenType type) {
        if (type == PLUS)
            return Command.ADD;
        if (type == MINUS)
            return Command.SUB;
        if (type == LT)
            return Command.LT;
        if (type == GT)
            return Command.GT;
        if (type == EQ)
            return Command.EQ;
        if (type == AND)
            return Command.AND;
        if (type == OR)
            return Command.OR;
        return null;
    }

    // boolean isOperator(Token c) {
    // String isSymbol = "+-*/&|><=~";
    // return (isSymbol.contains(c.lexeme));
    // }

}
