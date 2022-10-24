import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.util.stream.IntStream;
import java.util.List;
import java.util.ArrayList;

class Scanner {
  
  private char[] content;
  private int state;
  private int index;
  private int indexNext=1;
  
  public Scanner(String input){
    try{
      String text = new String(Files.readAllBytes(Paths.get(input)),StandardCharsets.UTF_8);
      System.out.println("DEBUG ------");
      System.out.println(text);
      System.out.println("------");
      content = text.toCharArray();
      index=0;
}
    catch(Exception ex){
      ex.printStackTrace();
      
    }
  }
  
  private boolean isIntConst(char c){//checa inteiro
    return c>='0' && c<='9';
  }
  

  private boolean isAlpha1(char c){//checa se é palavra
    return (c>='a' && c<='z')||(c>='A' && c<='Z');
  }
  
  private boolean isAlpha2(char c){//checa se é palavra
    return (c>='a' && c<='z')||(c>='A' && c<='Z')||c=='_';
  }


  private boolean isStringConst(char c){
    return (c=='"');
  }
  
  private boolean isSpace(char c){//checa caracteres a ignorar
    return c==' '|| c=='\t'||c=='\n'||c=='\r';
  }


  private boolean isBreakLine(char c){
    return c=='\n';
  }

  private boolean isSymbol(char c){
    String symbols = "{}()[].,;+-*/&|<>=~";
    char[] list = symbols.toCharArray();
     for (int i = 0; i < list.length; i++){
       if(c==list[i]){
         return true;
       }
     }
    return false;
  }

  
  private boolean isKeyword(String c){
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

  private char futureChar(){
    if(isEndOfFile()){
      return '\0';
    }
    else if(!(index== content.length)){
      return content[indexNext++];
    }
    return content[indexNext];
  }


  
  
  private void back(){//volta estad0
    index--;
  }

  private boolean isComent(char c, char d){
    return (c=='\'' && d=='\'');
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
      //futureChar = futureChar();
      //System.out.println("char atual "+currentChar);
     // System.out.println("char futuro "+futureChar);
      switch(state){
        case 0:
          if(isStringConst(currentChar)){
            term+=currentChar;
            state = 1;
          }
          else if(isEndOfFile()){
            return null;
          }
          
          else if(isIntConst(currentChar)){
            state = 8;
            term+=currentChar;
          }
          
          else if(isSymbol(currentChar)){
            state = 10;
            term+=currentChar;
          }
            
          else if(!isSymbol(currentChar)&&!isAlpha1(currentChar)&&!isAlpha2(currentChar)&&!isSpace(currentChar)){
            state = -1;
            term+=currentChar;
          }
          else if(isAlpha1(currentChar)){
            state = 3;
            term+=currentChar;
          }
          break;
          
        case 1:
          if(!isStringConst(currentChar)){
            term+=currentChar;
            state = 1;
          }else{
            term+=currentChar;
            state = 2;      
          }
          break;
          
        case 2:
          back();
          token = new Token();
          token.setType(Token.stringConst);
          token.setText(term);
          state=0;
          return token;
          
        case 8:
          if(isIntConst(currentChar)){
            state = 8;
            term+=currentChar;
          }else{
            back();
            token = new Token();
            token.setType(Token.intConst);
            token.setText(term);
            state=0;
            term+=currentChar;
            return token;
          }
          
        case 10:
          back();
          token = new Token();
          token.setType(Token.symbol);
          token.setText(term);
          state=0;
          return token;
          
        case -1:
          back();
          token = new Token();
          token.setType(Token.ilegal);
          token.setText(term);
          state=0;
          return token;
          
        case 3:
          if(isAlpha1(currentChar)){
            state = 3;
            term+=currentChar;
          }else{
            state = 4;
            back();
          }
          break;
          
        case 4:
          if(isKeyword(term)){
            back();
            token = new Token();
            token.setType(Token.keyword);
            token.setText(term);
            state=0;
            
            return token;
          }else{
            back();
            token = new Token();
            token.setType(Token.identifier);
            token.setText(term);
            state=0;
            return token;
          }
        case 11:
          System.out.println("AQUI");
          if(currentChar =='\n'){
            state = 0;
            
          }else{
            state = 11;
           // System.out.println("futurechar"+futureChar);
          }
          
      }
      
    }
  }
}