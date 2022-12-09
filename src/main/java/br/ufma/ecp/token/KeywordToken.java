package br.ufma.ecp.token;

public class KeywordToken extends Token {

    public KeywordToken(TokenType type, int line) {
        super(type, line);
    }

    public String toString() {
        return "<keyword> " + type.value + " </keyword>";
    }

}
