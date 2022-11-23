package br.ufma.ecp;

import java.io.EOFException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementScanner14;

import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;

public class Scanner {

    private byte[] input;
    private int current;
    private int start;


    public Scanner (byte[] input) {
        this.input = input;
        current = 0;
        start = 0;
    }
    // + - numeros (1, 12, 678)
    public Token nextToken () {

        skipWhitespace();

        start = current;
        char ch = peek();

        if (Character.isDigit(ch)) {
            return number();
        }

        if (Character.isLetter(ch)) {
            return identifier();
        }



        switch (ch) {

        
            case '"':
                return string();

            case '/':
                if (peekNext() == '/') {
                    skipLineComments();
                    return nextToken();
                } else if (peekNext() == '*') {
                    skipBlockComments();
                    return nextToken();
                }
                else {
                    advance();
                    return new Token (TokenType.SLASH,"/");
                }

            case '+':
                advance();
                return new Token (TokenType.PLUS,"+");
            case '-':
                advance();
                return new Token (TokenType.MINUS,"-"); 
            case '*':
                advance();
                return new Token (TokenType.ASTERISK,"*"); 
            case '.':
                advance();
                return new Token (TokenType.DOT,"."); 
            case '&':
                advance();
                return new Token (TokenType.AND,"&"); 
            case '|':
                advance();
                return new Token (TokenType.OR,"|"); 
            case '~':
                advance();
                return new Token (TokenType.NOT,"~"); 


            case '>':
                advance();
                return new Token (TokenType.GT,">"); 
            case '<':
                advance();
                return new Token (TokenType.LT,"<"); 
            case '=':
                advance();
                return new Token (TokenType.EQ,"="); 

            case '(':
                advance();
                return new Token (TokenType.LPAREN,"("); 
            case ')':
                advance();
                return new Token (TokenType.RPAREN,")"); 
            case '{':
                advance();
                return new Token (TokenType.LBRACE,"{"); 
            case '}':
                advance();
                return new Token (TokenType.RBRACE,"}"); 
            case '[':
                advance();
                return new Token (TokenType.LBRACKET,"["); 
            case ']':
                advance();
                return new Token (TokenType.RBRACKET,"]"); 
            case ';':
                advance();
                return new Token (TokenType.SEMICOLON,";"); 
            case ',':
                advance();
                return new Token (TokenType.COMMA,","); 

            case 0:
                return new Token(TokenType.EOF, "EOF");  
            default:
                advance(); 
                return new Token(TokenType.ILLEGAL, Character.toString(ch));
        }


      

    }


    private void skipBlockComments() {
        boolean endComment = false;
        advance();

        while (!endComment) {
            advance();
            char ch = peek();

            if ( ch == 0) { // eof
                System.exit(1);
            }
    
         
            if (ch == '*') {

               for (ch = peek(); ch == '*';  advance(), ch = peek()) ;

             
                if (ch == '/') {
                    endComment = true;
                    advance();
                }
            }

        }

    }

    
    private void skipLineComments() {
  
        for (char ch = peek(); ch != '\n' && ch != 0;  advance(), ch = peek()) ;
    }

    private void skipWhitespace() {
        char ch = peek();
        while (ch == ' ' || ch == '\r' || ch == '\t' || ch == '\n') {
            advance();
            ch = peek();
        }
    }

    private boolean isAlphaNumeric(char ch) {
        return Character.isLetter(ch) || Character.isDigit(ch);
    }

    private Token string () {
        advance();
        start = current;
        while (peek() != '"' && peek() != 0) {
            advance();
        }
        String s = new String(input, start, current-start, StandardCharsets.UTF_8);
        Token token = new Token (TokenType.STRING,s);
        advance();
        return token;
    }

    private Token identifier() {
        while (isAlphaNumeric(peek()) ) {
            advance();
        }
        String id = new String(input, start, current-start, StandardCharsets.UTF_8);
        TokenType type = Token.keyword(id);
        if (type == null) type = TokenType.IDENTIFIER;
        Token token = new Token (type,id);
        return token;
    }

    private Token number () {
        while (Character.isDigit(peek())) {
            advance();
        }
        String s = new String(input, start, current-start, StandardCharsets.UTF_8);
        Token token = new Token (TokenType.NUMBER,s);
        return token;
    }

    private void advance () {
        char ch = peek();
        if (ch != 0) {
            current++;
        }
    }

    private char peek () {
         if ( current < input.length) {
             return (char)input[current];
         } else {
             return 0;
         }
    }

    private char peekNext () {
        int next = current + 1;
        if ( next  < input.length) {
            return (char)input[next];
        } else {
            return 0;
        }
   }


    
}
