package br.ufma.ecp.token;

public class IlegalToken extends Token {

    char c;

    public IlegalToken(TokenType type, char c, int line) {
        super(type, line);
        this.c = c;
    }

}
