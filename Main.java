/* 
Categorias de tokens
<tokens>
1- <keyword> if </keyword>
  'class' | 'constructor' | 'function' |
'method' | 'field' | 'static' | 'var' | 'int' |
'char' | 'boolean' | 'void' | 'true' | 'false' |
'null' | 'this' | 'let' | 'do' | 'if' | 'else' |
'while' | 'return’


2- <symbol> ( </symbol>
   '{' | '}' | '(' | ')' | '[' | ']' | '. ' | ', ' | '; ' | '+' | '-' | '*' |
'/' | '&' | '|' | '<' | '>' | '=' | '~'

3- <identifier> x </identifier>
  (a...z)|(A...Z|0..9|a...z)*

4- <intConst> 0 </intConst>
  (0...9)+

5- <stringConst> negative </stringConst>
</tokens>
*/
class Main {
  public static void main(String[] args) {
   Scanner input = new Scanner("testeJack1.txt");
    Token token = null;
    
    do {
      token = input.nextToken();
      if(token != null){
        System.out.println(token);
      }
    } while(token!=null);
  }
}