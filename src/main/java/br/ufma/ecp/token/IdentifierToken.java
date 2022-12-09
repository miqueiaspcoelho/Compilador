package br.ufma.ecp.token;

public class IdentifierToken extends Token {

    String lexeme;

    public IdentifierToken(String lexeme, int line) {
        super(TokenType.IDENTIFIER, line);
        this.lexeme = lexeme;

    }

    public String toString() {
        return "<identifier> " + lexeme + " </identifier>";
    }

    public String value() {
        return lexeme;
    }

}
