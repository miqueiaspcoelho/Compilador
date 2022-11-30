package br.ufma.ecp.token;

public class StringToken extends Token {

    String lexeme;

    public StringToken(String lexeme, int line) {
        super(TokenType.STRING, line);
        this.lexeme = lexeme;

    }

    public String toString() {
        return "<string> " + lexeme + " </string>";
    }

    public String value() {
        return lexeme;
    }

}
