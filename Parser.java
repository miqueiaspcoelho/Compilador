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
    statements(currentToken);
   
  }
  

  //checa declarações
  public void statements(Token currentToken){
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

  //token esperado
  private void expectToken(Token currentToken, String tokenExpect){
    if(currentToken.getText().equals(tokenExpect) || currentToken.getType().equals(tokenExpect)){
      //System.out.println(currentToken.getText());
      xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
      nextToken();
      return;
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

  //expressionList - falta revisar algumas coisas
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

  //subrotineCall - falta revisar algumas coisas
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
        countOp-=1;
      break;

      case "stringConstant":
        printNonTerminal("term");
        expectToken(currentToken, "stringConstant");
        printNonTerminal("/term");
        countOp-=1;
      break;

      case "identifier":
        printNonTerminal("term");
        expectToken(currentToken, "identifier");
        printNonTerminal("/term");
        countOp-=1;
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
            countOp-=1;
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
        countOp+=1;
        return true;
      }else if(aux.equals("&")||aux.equals("|")||aux.equals(">")||aux.equals("<")||aux.equals("=")){
        countOp+=1;
        return true;
      }
    }
    return false;
  }

  //'expression' -> term(op term)*';'
  void parserExpression(){
   
    printNonTerminal("expression");
    parserTerm();
    while(isOperator(currentToken)==true && countOp==0){
        printNonTerminal("operation");
        expectToken(currentToken,currentToken.getText());
        printNonTerminal("/operation");
        parserTerm();
    }
    printNonTerminal("/expression");
  }
}