class Parser {
  private  Scanner scanner;//léxico é entrada do analisador sintático - chamando por demanda
  private Token currentToken;//token atual
  private StringBuilder xmlOutput = new StringBuilder();//arquivo xml
  private  int countOp = 0;
  
  //construtor
  public Parser (Scanner scanner){
    this.scanner = scanner;
  }

  //pega próximo token
  private void nextToken() {
    currentToken = scanner.nextToken();    
  }


  //inicia parser
  void start(){
    currentToken = scanner.nextToken();
     //System.out.println(currentToken.getText());
    //parserStatements();
    if(currentToken!=null){
       parserClass();
    }
   
   
  }

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

  //checa declarações
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
  
//letStatement -> 'let' identifier( '[' expression ']' )? '=' expression ';’
  void parserLet(){
    printNonTerminal("letStatement");
    expectToken(currentToken, "let");
    expectToken(currentToken, "identifier");
    if(currentToken.getText().equals("[")){
      expectToken(currentToken,"[");
      parserExpression();
      expectToken(currentToken,"]");
        }
    expectToken(currentToken, "=");
    parserExpression();
    expectToken(currentToken, ";");
    printNonTerminal("/letStatement");
  }

  //token esperado - mudar para tirar currentToken daqui de dentro, pois, é variável global
  
  private void expectToken(Token currentToken, String tokenExpect){
    if(currentToken.getText().equals(tokenExpect) || currentToken.getType().equals(tokenExpect)){
      //System.out.println(currentToken.getText());
      if(currentToken!=null){
        xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
      nextToken();
      return;  
      }
      
    }else{
      throw new Error("Syntax error - expected "+tokenExpect+" found " + currentToken.getText());
    }
  }

  //adiciona ao xml os não terminais
  private void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
  }

  //retornando em string o xml
  public String XMLOutput() {
    return xmlOutput.toString();
  }

  void parserClass(){
    printNonTerminal("class");
    expectToken(currentToken,"class");
    expectToken(currentToken,"identifier");
    expectToken(currentToken,"{");
    while(currentToken.getText().equals("static")||currentToken.getText().equals("field")){
      parserClassVarDec();
    }
    while(currentToken.getText().equals("constructor")||
      currentToken.getText().equals("function")||
      currentToken.getText().equals("method")){
      //System.out.println(currentToken.getText());
      if(currentToken!=null){
        parserSubroutineDec();
      }
        
    }
    expectToken(currentToken,"}");
    printNonTerminal("/class"); 
    if(currentToken!=null){
      parserClass();
    }
  }

  void parserVarDec(){
    printNonTerminal("varDec");
    expectToken(currentToken,"var");
    // 'int' | 'char' | 'boolean' | className
    switch(currentToken.getType()){
        case "keyword":
          if(currentToken.getText().equals("int")||
             currentToken.getText().equals("char")||
            currentToken.getText().equals("boolean")){
            expectToken(currentToken,currentToken.getText());
            }
        break;
        case "identifier":
          expectToken(currentToken,currentToken.getText());
        break;
      }
    expectToken(currentToken,"identifier");
    
    while (currentToken.getText().equals(",")) {
      expectToken(currentToken,",");
      expectToken(currentToken,"identifier");
    }

    expectToken(currentToken,";");
    printNonTerminal("/varDec");
  }

  void parserParameterList(){
    //System.out.println(currentToken.getText());
    printNonTerminal("parameterList");
    
    if(!currentToken.getText().equals(")")){
      
      //if(currentToken.getText().equals("static")||
       //currentToken.getText().equals("field")){
      expectToken(currentToken, currentToken.getText());
      switch(currentToken.getType()){
        case "keyword":
          if(currentToken.getText().equals("int")||
             currentToken.getText().equals("char")||
            currentToken.getText().equals("boolean")){
            expectToken(currentToken,currentToken.getText());
            }
        break;
        case "identifier":
          expectToken(currentToken,currentToken.getText());
        break;
      }
    //}
    }
    while(currentToken.getText().equals(",")){
      expectToken(currentToken,currentToken.getText());
      //if(currentToken.getText().equals("static")||
      //currentToken.getText().equals("field")){
        
       // expectToken(currentToken, currentToken.getText());
        switch(currentToken.getType()){
          case "keyword":
            if(currentToken.getText().equals("int")||
               currentToken.getText().equals("char")||
              currentToken.getText().equals("boolean")){
              expectToken(currentToken,currentToken.getText());
              }
          break;
          case "identifier":
            expectToken(currentToken,currentToken.getText());
          break;
        }
      //}
      expectToken(currentToken,"identifier");
    }
    printNonTerminal("/parameterList");
  }


  void parserSubroutineDec(){
    printNonTerminal("subroutineDec");
    //expectPeek(CONSTRUCTOR, FUNCTION, METHOD);
    if(currentToken.getText().equals("constructor")||
      currentToken.getText().equals("function")||
      currentToken.getText().equals("method")){
      expectToken(currentToken, currentToken.getText());
      // 'int' | 'char' | 'boolean' | className
        switch(currentToken.getType()){
          case "keyword":
            if(currentToken.getText().equals("int")||
              currentToken.getText().equals("char")||
              currentToken.getText().equals("boolean")||
              currentToken.getText().equals("void")
              ){
                expectToken(currentToken,currentToken.getText());
            }
          break;
          case "identifier":
            expectToken(currentToken,currentToken.getText());
          break; 
        }
    }
    expectToken(currentToken,"identifier");
    expectToken(currentToken,"(");
    parserParameterList();
    expectToken(currentToken,")");
    parserSubroutineBody();//falta implementar

    printNonTerminal("/subroutineDec");
  }


  void parserSubroutineBody(){
    expectToken(currentToken,"{");
    parserStatements();
    expectToken(currentToken,"}");
  }
  
  //classVarDec → ( 'static' | 'field' ) type varName ( ',' varName)* ';'
  void parserClassVarDec(){
    
    printNonTerminal("classVarDec");
    if(currentToken.getText().equals("static")||
       currentToken.getText().equals("field")){
      
      expectToken(currentToken, currentToken.getText());
      switch(currentToken.getType()){
        case "keyword":
          if(currentToken.getText().equals("int")||
             currentToken.getText().equals("char")||
            currentToken.getText().equals("boolean")){
            expectToken(currentToken,currentToken.getText());
            }
        break;
        case "identifier":
          expectToken(currentToken,currentToken.getText());
        break;
      }
      expectToken(currentToken,"identifier");
    }
    while(currentToken.getText().equals(",")){
      //System.out.println(currentToken.getText());
      expectToken(currentToken,currentToken.getText());
      expectToken(currentToken,"identifier");
    }
    expectToken(currentToken, ";");
    printNonTerminal("/classVarDec");
  }

  //if
  void parserIf () {
    printNonTerminal("ifStatement");
    expectToken(currentToken,"if");
    expectToken(currentToken,"(");
    parserExpression();
    expectToken(currentToken,")");
    expectToken(currentToken,"{");
    parserStatements();
    expectToken(currentToken,"}");
    printNonTerminal("/ifStatement");
    }

  
  // 'while' '(' expression ')' '{' statements '}'
  void parserWhile(){
    printNonTerminal("whileStatement");
    expectToken(currentToken,"while");
    expectToken(currentToken,"(");
    parserExpressionList();
    expectToken(currentToken,")");
    expectToken(currentToken,"{");
    parserStatements();
    expectToken(currentToken,"}");
    printNonTerminal("/whileStatement");
    }

  

  //'do' subrotineCall ';' com alguns probleminhas ainda
  void parserDo(){
    printNonTerminal("doStatement");
    expectToken(currentToken, "do");
    expectToken(currentToken, "identifier");
    parserSubrotineCall();
    expectToken(currentToken, ";");
    printNonTerminal("/doStatement");
  }

  //ReturnStatement -> 'return' expression? ';'
  void parserReturn(){
    printNonTerminal("return");
    expectToken(currentToken, "keyword");
    if(!currentToken.getText().equals(";")){
      parserExpression();
    }
    expectToken(currentToken, ";");
    printNonTerminal("/return");
  }

  //'expression' -> term(op term)*';'
  void parserExpression(){
    printNonTerminal("expression");
      parserTerm();
      while(isOperator(currentToken)==true){
        printNonTerminal("operation");
        expectToken(currentToken,currentToken.getText());
        printNonTerminal("/operation");
        parserTerm();
      }
    
    printNonTerminal("/expression");
  }
  
  //expressionList
  void parserExpressionList(){
    
    printNonTerminal("expressionList");
    if(!currentToken.getText().equals(")")){
      parserExpression();
     
    }
    while(currentToken.getText().equals(",")){
      expectToken(currentToken,",");
      parserExpression();
      
    }
    printNonTerminal("/expressionList");
  }

  //subrotineCall
  void parserSubrotineCall(){
    if(currentToken.getText().equals("(")){
      expectToken(currentToken,"(");
      parserExpressionList();
      expectToken(currentToken,")");
    }
    else{
      expectToken(currentToken,".");
      expectToken(currentToken,"identifier");
      expectToken(currentToken,"(");
      parserExpressionList();
      expectToken(currentToken,")");
    }
  }
  
  //termo - as demais partes dele estão sendo implementadas
  void parserTerm(){
    switch(currentToken.getType()){
      case "integerConstant":
        printNonTerminal("term");
        expectToken(currentToken, "integerConstant");
        printNonTerminal("/term");
        countOp=0;
      break;

      case "stringConstant":
        printNonTerminal("term");
        expectToken(currentToken, "stringConstant");
        printNonTerminal("/term");
        countOp=0;
      break;

      case "identifier":
        printNonTerminal("term");
        expectToken(currentToken, "identifier");
        printNonTerminal("/term");
        countOp=0;
      break;

      case "keyword":
        switch(currentToken.getText()){
          case "true":
          case "false":
          case "null":
          case "this":
            printNonTerminal("term");
            expectToken(currentToken, currentToken.getText());
            printNonTerminal("/term");
            countOp=0;
          break;
          default:
            ;
        }
        
    }  
  }

  //checando se é um operador válido
  boolean isOperator(Token c){
    String aux = c.getText();
    if(c.getType().equals("symbol")){
      if(aux.equals("+") || aux.equals("-")||aux.equals("*")||aux.equals("/")){
        countOp=1;
        return true;
      }else if(aux.equals("&")||aux.equals("|")||aux.equals(">")||aux.equals("<")||aux.equals("=")){
        countOp=1;
        return true;
      }
    }
    return false;
  }


}