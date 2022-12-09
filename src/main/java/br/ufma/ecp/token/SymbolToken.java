package br.ufma.ecp.token;

public class SymbolToken extends Token {

    public SymbolToken(TokenType type, int line) {
        super(type, line);
    }

    public String toString() {
        var valor = type.value;

        // Os símbolos <, >, ", e & são impressos como &lt;  &gt;  &quot; e &amp; Para
        // não conflitar com o significado destes símbolos no XML
        if (valor == ">") {
            valor = "&gt;";
        } else if (valor == "<") {
            valor = "&lt;";
        } else if (valor == "\"") {
            valor = "&quot;";
        } else if (valor == "&") {
            valor = "&amp;";
        }

        return "<symbol> " + valor + " </symbol>";
    }

}
