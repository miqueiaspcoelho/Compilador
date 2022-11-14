import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
//import java.util.*;
//import java.lang.*;
//import java.io.*;
//import java.util.stream.IntStream;
//import java.util.List;
import java.util.ArrayList;

class Scanner {
  
  private char[] content;
  private int state;
  private int index;
  private int indexNext=1;
  
  public Scanner(String input){
    try{
      String text = new String(Files.readAllBytes(Paths.get(input)),StandardCharsets.UTF_8);
      //System.out.println("DEBUG ------");
      //System.out.println(text);
      //System.out.println("------");
      content = text.toCharArray();
      index=0;
}
    catch(Exception ex){
      ex.printStackTrace();
      
    }
  }
  
  private boolean isIntConst(char c){//checa inteiro
    return Character.isDigit(c);
  }
  

  private boolean isAlpha1(char c){//checa se é palavra
    return (c>='a' && c<='z')||(c>='A' && c<='Z');
  }
  

  private boolean isStringConst(char c){
    return (c=='"');
  }
  
  private boolean isSpace(char c){//checa caracteres a ignorar
    return c==' '|| c=='\t'||c=='\n'||c=='\r';
  }

  private boolean isSymbol(char c){//checa se é um símbolo válido - {/}
    String symbols = "{}()[].,;+-*&|<>=~";
    char[] list = symbols.toCharArray();
     for (int i = 0; i < list.length; i++){
       if(c==list[i]){
         return true;
       }
     }
    return false;
  }

  
  private boolean isKeyword(String c){//checa palavra chave
    boolean r=false;
    ArrayList<String> keywords = new ArrayList<>();
    keywords.add("class");
    keywords.add("constructor");
    keywords.add("function");
    keywords.add("method");
    keywords.add("field");
    keywords.add("static");
    keywords.add("var");
    keywords.add("int");
    keywords.add("char");
    keywords.add("boolean");
    keywords.add("void");
    keywords.add("true");
    keywords.add("false");
    keywords.add("null");
    keywords.add("this");
    keywords.add("let");
    keywords.add("do");
    keywords.add("if");
    keywords.add("else");
    keywords.add("while");
    keywords.add("return");
    for (int i = 0; i < keywords.size(); i++){
      if(c.equals((keywords.get(i)))){
        
        r=true;
        return r;
      }else{
        
        r=false;
      }
    }
    return r;
  }
  
  private boolean isEndOfFile(){//checa fim de arquivo
    return index >= content.length;
  }
  

  private char nextChar(){//proximo caractere
    if(isEndOfFile()){
      return '\0';
    }
    return content[index++];
  }

  private char futureChar(){//caractere de antecipaçãoi
    if(indexNext<content.length){
      return content[indexNext++];
    }
    return '\0';
  }

  private boolean isComent(char c, char d){//checa se é comentário
    return (c=='/' && d=='/');
  }

  private boolean beginComentBlock(char c, char d){//começo de comentário
    return (c=='/' && d=='*');
  }
  private boolean endComentBlock(char c, char d){ //final de comentário
    return (c=='*' && d=='/');
  }

  private void back(){//volta estado
    index--;
  }

  private void backFuture(){//volta estado
    indexNext--;
  }

  
  public Token nextToken(){//esse aqui é o brabo, faz a mágica
    char currentChar,futureChar;
    Token token;
    String term="";
    state = 0;

    if(isEndOfFile()){
      return null;
    }
     
    while(true){
      currentChar = nextChar();
      futureChar = futureChar();
      switch(state){
        case 0:
          if(isEndOfFile()){
            return null;
          }

          else if(isSpace(currentChar)){
            state=0;
          }
            
          else if(isIntConst(currentChar)){ 
            state = 8;
            term+=currentChar;
          }

          else if(isStringConst(currentChar)){
            //term+=currentChar;
            state = 1;
          }

          else if(isAlpha1(currentChar)){
            state = 3;
            term+=currentChar;
          }
          else if(currentChar=='/'){
            if(isComent(currentChar,futureChar)){
              state = 11; 
            }
            else if(beginComentBlock(currentChar,futureChar)){
              state = 12;
            }
            else if(futureChar!='\n'){
              //System.out.println("atual= "+currentChar);
              //System.out.println("proximo= "+futureChar);
              state = 10;
              term+=currentChar;  
            }        
          }
          else if(isSymbol(currentChar)){
            //System.out.println("atual "+currentChar);
            //System.out.println("próximo " + futureChar);
            state = 10;
            term+=currentChar;
          }
          
          else{
            state = -1;
          }
          break;
/*Finaliza estado 0*/
          
        case 1:
          if(!isStringConst(currentChar)){
            term+=currentChar;
            state = 1;
          }else{
            //term+=currentChar;
            state = 2;      
          }
        break;
/*Finaliza estado 1*/
          
        case 2:
          back();
          backFuture();
          token = new Token();
          token.setType(Token.stringConst);
          token.setText(term);
          state=0;
          return token;
/*Finaliza estado 2*/
          
        case 3:
          if(isAlpha1(currentChar)){
            state = 3;
            term+=currentChar;
          }else{
            state = 4;
            back();
            backFuture();
          }
        break;
/*Finaliza estado 3*/
          
        case 4:
          if(isKeyword(term)){
            back();
            backFuture();
            token = new Token();
            token.setType(Token.keyword);
            token.setText(term);
            state=0;
            return token;
            
          }else{
            back();
            backFuture();
            token = new Token();
            token.setType(Token.identifier);
            token.setText(term);
            state=0;
            return token;          }
/*Finaliza estado 4*/
          
        case 8:
          if(isIntConst(currentChar)){
            term+=currentChar;
            state = 8;
          }else{
            back();
            backFuture();
            token = new Token();
            token.setType(Token.intConst);
            token.setText(term);
            state=0;
            term+=currentChar;
            return token;
          }
        break;
/*Finaliza estado 8*/
          
       case 10:
          back();
          backFuture();
          token = new Token();
          token.setType(Token.symbol);
          token.setText(term);
          state=0;
          return token;
/*Finaliza estado 10*/
          
        case -1:
          back();
          backFuture();
          token = new Token();
          token.setType(Token.ilegal);
          token.setText(term);
          state=0;
          return token;
/*Finaliza estado -1*/

        case 11:
          if(currentChar=='\n'){
            state = 0;
          }else{
            state = 11;
          }
        break;
/*Finaliza estado 11*/
        case 12:
          if(!endComentBlock(currentChar,futureChar)){
            state = 12;
          }else{
            state = 0;
          }
        break;
      }
      
    }
  }
}