package br.ufma.ecp.token;

public class IntegerToken extends Token {

    String lexeme;

    public IntegerToken(String lexeme, int line) {
        super(TokenType.INTEGER, line);
        this.lexeme = lexeme;
    }

    public String toString() {
        return "<integerConstant> " + lexeme + " </integerConstant>";
    }

    public String value() {
        return lexeme;
    }

}
