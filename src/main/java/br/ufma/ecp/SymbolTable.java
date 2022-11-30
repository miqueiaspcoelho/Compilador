package br.ufma.ecp;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    // kind enum
    public enum Kind {
        STATIC, FIELD, ARG, VAR
    };

    /*
     * O objetivo é ser possível ter classes que atuam como portadores transparentes
     * de dados imutáveis.
     * Os registros podem ser considerados tuplas nominais.
     * Ou seja, após criado, um record não pode mais ser alterado.
     */
    public static record Symbol(String name, String type, Kind kind, int index) {
    }

    private Map<String, Symbol> classScope;
    private Map<String, Symbol> subroutineScope;
    private Map<Kind, Integer> countVars;

    // criando uma tabela de símbolos
    public SymbolTable() {
        classScope = new HashMap<>();
        subroutineScope = new HashMap<>();
        countVars = new HashMap<>();

        countVars.put(Kind.ARG, 0);
        countVars.put(Kind.VAR, 0);
        countVars.put(Kind.STATIC, 0);
        countVars.put(Kind.FIELD, 0);

    }

    public void startSubroutine() {

        subroutineScope.clear();
        countVars.put(Kind.ARG, 0);
        countVars.put(Kind.VAR, 0);

    }

    private Map<String, Symbol> scope(Kind kind) {
        if (kind == Kind.STATIC || kind == Kind.FIELD) {
            return classScope;
        } else {
            return subroutineScope;
        }
    }

    void define(String name, String type, Kind kind) {

        Map<String, Symbol> scopeTable = scope(kind);
        if (scopeTable.get(name) != null)
            throw new RuntimeException("variable already defined");// checa se variável já existe

        Symbol s = new Symbol(name, type, kind, varCount(kind));
        scopeTable.put(name, s);// coloca variável na tabela de símbolos
        countVars.put(kind, countVars.get(kind) + 1);

    }

    public Symbol resolve(String name) {
        Symbol s = subroutineScope.get(name);
        if (s != null) {
            return s;
        } else {
            return classScope.get(name);
        }

    }

    int varCount(Kind kind) {
        return countVars.get(kind);
    }

}
