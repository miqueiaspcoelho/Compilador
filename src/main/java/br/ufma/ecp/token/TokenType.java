package br.ufma.ecp.token;

import java.util.Arrays;

public enum TokenType {

    STRING(),

    INTEGER(),

    IDENTIFIER(),

    // keywords
    WHILE("while"), CLASS("class"), CONSTRUCTOR("constructor"), FUNCTION("function"),
    METHOD("method"), FIELD("field"), STATIC("static"), VAR("var"), INT("int"),
    CHAR("char"), BOOLEAN("boolean"), VOID("void"), TRUE("true"), FALSE("false"),
    NULL("null"), THIS("this"), LET("let"), DO("do"), IF("if"),
    ELSE("else"), RETURN("return"),

    // Symbols
    PLUS("+"),
    EQ("="),
    MINUS("-"),
    ASTERISK("*"),
    SLASH("/"),
    AND("&"),
    OR("|"),
    NOT("~"),

    LT("<"),
    GT(">"),

    DOT("."),
    COMMA(","),
    SEMICOLON(";"),
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),
    LBRACKET("["),
    RBRACKET("]"),

    EOF();

    private TokenType() {
    }

    private TokenType(String value) {
        this.value = value;
    }

    public String value;

    public static TokenType fromValue(String value) {
        return Arrays.stream(TokenType.values())
                .filter(symbolType -> symbolType.value != null && symbolType.value.equals(value))
                .findFirst()
                .orElse(null);
    }

    static public boolean isSymbol(char c) {
        String symbols = "{}()[].,;+-*/&|<>=~";
        return symbols.indexOf(c) > -1;
    }

    static public boolean isOperator(TokenType type) {
        return "+-*/<>=~&|".contains(type.value);
    }

    static public TokenType keyword(String value) {
        return fromValue(value);
    }

}
