package br.ufma.ecp.token;

public class Token {

    public final TokenType type;
    public final int line;

    public Token(TokenType type, int line) {
        this.type = type;
        this.line = line;
    }

    public String value() {
        return type.value;
    }
}
