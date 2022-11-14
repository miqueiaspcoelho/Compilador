class Parser {
  private  Scanner scanner;//léxico é entrada do analisador sintático - chamando por demanda
  private Token currentToken;//token atual
  private StringBuilder xmlOutput = new StringBuilder();//arquivo xml

  //construtor
  public Parser (Scanner scanner){
    this.scanner = scanner;
  }

  //pega próximo token
  private void nextToken() {
    currentToken = scanner.nextToken();    
  }

  //adiciona ao xml os não terminais
  private void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
  }

  //retornando em string o xml
  public String XMLOutput() {
    return xmlOutput.toString();
  }

  //token esperado - mudar para tirar currentToken daqui de dentro, pois, é variável global
  private void expectToken(String tokenExpect){
    if(currentToken.getText().equals(tokenExpect) || currentToken.getType().equals(tokenExpect)){
      if(currentToken!=null){
        xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
        nextToken();
        return;  
      }
      
    }else{
      throw new Error("Syntax error - expected "+tokenExpect+" found " + currentToken.getText());
    }
  }

  //checando se é um operador válido
  boolean isOperator(Token c){  
    String isSymbol = "+-*/&|><=~";
    return (isSymbol.contains(c.getText()));
  }

  //inicia parser
  void start(){
    currentToken = scanner.nextToken();
    if(currentToken!=null){
       parserClass();
    }
  }

  //termo - integerConstant | stringConstant | keywordConstant | varName | varName '[' expression ']' | subroutineCall | '(' expression ')' | unaryOp term
  void parserTerm(){
    printNonTerminal("term");
    switch(currentToken.getType()){
      case "integerConstant":
        expectToken( "integerConstant");
      break;

      case "stringConstant":
        expectToken( "stringConstant");
      break;

      case "identifier":
        expectToken( "identifier");
        if(currentToken.getText().equals("(")||currentToken.getText().equals(".")){
          parserSubrotineCall();
        }else{
          if(currentToken.getText().equals("[")){
            expectToken( "[");
            parserExpression();
            expectToken( "]");
          }
        }
      break;

      case "keyword":
        switch(currentToken.getText()){
          case "true":
          case "false":
          case "null":
          case "this": 
            expectToken( currentToken.getText());
          break;
        }
        
      case "symbol":    
        if(currentToken.getText().equals("(")){ 
          expectToken("(");
          parserExpression();
          expectToken(")");
        }
        else if(currentToken.getText().equals("-")||currentToken.getText().equals("~")){
          expectToken(currentToken.getText());
          parserTerm();
        }   
      break;
        
      default:
        ;      
    }
    printNonTerminal("/term");
  }
  
  //statements - statements*
  void parserStatements () {
    printNonTerminal("statements");
    while (currentToken.getText().equals("while") ||
    currentToken.getText().equals("if") ||
    currentToken.getText().equals("let") ||
    currentToken.getText().equals("do") ||
    currentToken.getText().equals("return")) 
    {
      parserStatement(currentToken);
      if(currentToken==null){
        break;
      }
    }    
    printNonTerminal("/statements");
    }

  //statement - letStatement | ifStatement | whileStatement | doStatement | returnStatement
  public void parserStatement(Token currentToken){
    switch(currentToken.getText()){
      case "let":
        parserLet();
      break;
      case "return":
        parserReturn();
      break;
      case "do":
        parserDo();
      break;
      case "while":
        parserWhile();
      break;
      case "if":
        parserIf();
      break;  
    }
  }

/*-----------------------INICIO DOS STATEMENTS-------------------------------------------- */
  
//letStatement -> 'let' identifier( '[' expression ']' )? '=' expression ';’
  void parserLet(){
    printNonTerminal("letStatement");
    expectToken("let");
    expectToken("identifier");
    if(currentToken.getText().equals("[")){
      expectToken("[");
      parserExpression();
      expectToken("]");
        }
    expectToken("=");
    parserExpression();
    expectToken(";");
    printNonTerminal("/letStatement");
  }

//'if' '(' expression ')' '{' statements '}' ( 'else' '{' statements '}' )?
  void parserIf () {
    printNonTerminal("ifStatement");
    expectToken("if");
    expectToken("(");
    parserExpression();
    expectToken(")");
    expectToken("{");
    parserStatements();
    expectToken("}");
    if(currentToken.getText().equals("else")){
      expectToken("else");
      expectToken( "{");
      parserStatements();
      expectToken("}");
    }
    printNonTerminal("/ifStatement");
    }
  
  // 'while' '(' expression ')' '{' statements '}'
  void parserWhile(){
    printNonTerminal("whileStatement");
    expectToken("while");
    expectToken("(");
    parserExpressionList();
    expectToken(")");
    expectToken("{");
    parserStatements();
    expectToken("}");
    printNonTerminal("/whileStatement");
    }

  //'do' subrotineCall ';' com alguns probleminhas ainda
  void parserDo(){
    printNonTerminal("doStatement");
    expectToken("do");
    expectToken("identifier");
    parserSubrotineCall();
    expectToken(";");
    printNonTerminal("/doStatement");
  }

  //ReturnStatement -> 'return' expression? ';'
  void parserReturn(){
    printNonTerminal("returnStatement");
    expectToken("keyword");
    if(!currentToken.getText().equals(";")){
      parserExpression();
    }
    expectToken(";");
    printNonTerminal("/returnStatement");
  }
  
/*-----------------------FINAL DOS STATEMENTS-------------------------------------------- */

  
/*---------------------INICIO DOS CLASS-------------------------------------------------*/
  
  //classVarDec → ( 'static' | 'field' ) type varName ( ',' varName)* ';'
  void parserClassVarDec(){
    printNonTerminal("classVarDec");
    if(currentToken.getText().equals("static")||
       currentToken.getText().equals("field"))
    {  
      expectToken(currentToken.getText());
      switch(currentToken.getType()){
        case "keyword":
          if(currentToken.getText().equals("int")||
             currentToken.getText().equals("char")||
            currentToken.getText().equals("boolean")){
            expectToken(currentToken.getText());
            }
        break;
        case "identifier":
          expectToken(currentToken.getText());
        break;
      }
      expectToken("identifier");
    }
    while(currentToken.getText().equals(",")){
      expectToken(currentToken.getText());
      expectToken("identifier");
    }
    expectToken(";");
    printNonTerminal("/classVarDec");
  }

  //'class' className '{' classVarDec* subroutineDec* '}'
  void parserClass(){
    printNonTerminal("class");
    expectToken("class");
    expectToken("identifier");
    expectToken("{");
    while(currentToken.getText().equals("static")||currentToken.getText().equals("field")){
      parserClassVarDec();
    }
    while(currentToken.getText().equals("constructor")||
      currentToken.getText().equals("function")||
      currentToken.getText().equals("method")){
      if(currentToken!=null){
        parserSubroutineDec();
      }
    }
    expectToken("}");
    printNonTerminal("/class"); 
    if(currentToken!=null){
      parserClass();
    }
  }

/*---------------------FINAL DOS CLASS-------------------------------------------------*/


/*---------------------INICIO DAS SUBROUOTINES-------------------------------------------------*/

  //subroutineDec - ( 'constructor' | 'function' | 'method' ) ( 'void' | type) subroutineName '(' parameterList ')' subroutineBody
  void parserSubroutineDec(){
    printNonTerminal("subroutineDec");
    
    if(currentToken.getText().equals("constructor")||
      currentToken.getText().equals("function")||
      currentToken.getText().equals("method"))
    {
      expectToken(currentToken.getText());
      
        switch(currentToken.getType()){
          case "keyword":
            if(currentToken.getText().equals("int")||
              currentToken.getText().equals("char")||
              currentToken.getText().equals("boolean")||
              currentToken.getText().equals("void")
              ){
                expectToken(currentToken.getText());
            }
          break;
          case "identifier":
            expectToken(currentToken.getText());
          break; 
        }
    }
    expectToken("identifier");
    expectToken("(");
    parserParameterList();
    expectToken(")");
    parserSubroutineBody();
    printNonTerminal("/subroutineDec");
  }

  //subroutineBody - '{' varDec* statements '}'
  void parserSubroutineBody(){
    printNonTerminal("subroutineBody");
    expectToken("{");
    while(currentToken.getText().equals("var")){
      parserVarDec();
    }
    parserStatements();
    expectToken("}");
    printNonTerminal("/subroutineBody");
  }

  //subrotineCall - subroutineName '(' expressionList ')' | (className|varName) '.' subroutineName '(' expressionList ')'
  void parserSubrotineCall(){
    if(currentToken.getText().equals("(")){
      expectToken("(");
      parserExpressionList();
      expectToken(")");
    }
    else{
      expectToken(".");
      expectToken("identifier");
      expectToken("(");
      parserExpressionList();
      expectToken(")");
    }
  }
/*---------------------FINAL DAS SUBROUOTINES-------------------------------------------------*/

/*---------------------INICIO DOS EXPRESSIONS-------------------------------------------------*/

    //'expression' -> term(op term)*';'
  void parserExpression(){
    printNonTerminal("expression");
    parserTerm();
    while(isOperator(currentToken)==true){
      expectToken(currentToken.getText());
      parserTerm();
    }
    
    printNonTerminal("/expression");
  }

    //expressionList - (expression ( ',' expression)* )?
  void parserExpressionList(){
    printNonTerminal("expressionList");
    if(!currentToken.getText().equals(")")){
      parserExpression();
     
    }
    while(currentToken.getText().equals(",")){
      expectToken(",");
      parserExpression();    
    }
    printNonTerminal("/expressionList");
  }
  
  /*---------------------FINAL DOS EXPRESSIONS-------------------------------------------------*/


   /*---------------------VAR DEC e PARAMETER LIST-------------------------------------------------*/

  //varDec - 'var' type varName ( ',' varName)* ';'
  void parserVarDec(){
    printNonTerminal("varDec");
    expectToken("var");
    switch(currentToken.getType()){
        case "keyword":
          if(currentToken.getText().equals("int")||
             currentToken.getText().equals("char")||
            currentToken.getText().equals("boolean")){
            expectToken(currentToken.getText());
            }
        break;
        case "identifier":
          expectToken(currentToken.getText());
        break;
      }
    expectToken("identifier");
    
    while (currentToken.getText().equals(",")) {
      expectToken(",");
      expectToken("identifier");
    }

    expectToken(";");
    printNonTerminal("/varDec");
  }

  //parameterList - ((type varName) ( ',' type varName)*)?
  void parserParameterList(){
    printNonTerminal("parameterList");
    
    if(!currentToken.getText().equals(")"))
    { 
      expectToken(currentToken.getText());
      switch(currentToken.getType()){
        case "keyword":
          if(currentToken.getText().equals("int")||
             currentToken.getText().equals("char")||
            currentToken.getText().equals("boolean")){
            expectToken(currentToken.getText());
            }
        break;
        case "identifier":
          expectToken(currentToken.getText());
        break;
      }   
    }
    while(currentToken.getText().equals(",")){
      expectToken(currentToken.getText());  
        switch(currentToken.getType()){
          case "keyword":
            if(currentToken.getText().equals("int")||
               currentToken.getText().equals("char")||
              currentToken.getText().equals("boolean")){
              expectToken(currentToken.getText());
              }
          break;
          case "identifier":
            expectToken(currentToken.getText());
          break;
        }
      expectToken("identifier");
    }
    printNonTerminal("/parameterList");
  }

}