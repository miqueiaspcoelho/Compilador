class Parser {
  private  Scanner scanner;//léxico é entrada do analisador sintático - chamando por demanda
  private Token currentToken;//token atual
  private StringBuilder xmlOutput = new StringBuilder();//arquivo xml
  
  public Parser (Scanner scanner){//construtor
    this.scanner = scanner;
  }

  private void nextToken() {//pega próximo token
    currentToken = scanner.nextToken();    
  }


  void start(){//inicia parser
    currentToken = scanner.nextToken();
     //System.out.println(currentToken.getText());
    statements(currentToken);
   
  }

  public void statements(Token currentToken){//checa declarações
    switch(currentToken.getText()){
      case "let":
        parserLet();
      break;
    }
  }

  void parserLet(){//let
    printNonTerminal("letStatement");
    expectToken(currentToken, "let");
    expectToken(currentToken, "identifier");
    expectToken(currentToken, "=");
    parserExpression();
    expectToken(currentToken, ";");
    printNonTerminal("/letStatement");
  }

  private void expectToken(Token currentToken, String tokenExpect){//token esperado
    if(currentToken.getText().equals(tokenExpect) || currentToken.getType().equals(tokenExpect)){
      //System.out.println(currentToken.getText());
      xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
      nextToken();
      return;
    }else{
      throw new Error("Syntax error - expected "+tokenExpect+" found " + currentToken.getText());
    }
  }

  private void printNonTerminal(String nterminal) {//adiciona ao xml os não terminais
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
  }

  public String XMLOutput() {//retornando em string o xml
    return xmlOutput.toString();
  }

  void parserTerm(){//termo
    printNonTerminal("term");
    switch(currentToken.getType()){
      case "integerConstant":
        expectToken(currentToken, "integerConstant");
      break;
    }
    printNonTerminal("/term");
  }

  void parserOperation(){
    
    switch (currentToken.getText()){
      case "+":
        printNonTerminal("operation");
        expectToken(currentToken,"+");
        printNonTerminal("/operation");
        parserTerm();
        parserOperation();
      break;
      case "-":
        printNonTerminal("operation");
        expectToken(currentToken,"-");
        printNonTerminal("/operation");
        parserTerm();
        parserOperation();
      break;
      case "/":
        printNonTerminal("operation");
        expectToken(currentToken,"/");
        printNonTerminal("/operation");
        parserTerm();
        parserOperation();
      break;
      case "*":
        printNonTerminal("operation");
        expectToken(currentToken,"*");
        printNonTerminal("/operation");
        parserTerm();
        parserOperation();
      break;
      case "&":
        printNonTerminal("operation");
        expectToken(currentToken,"&");
        printNonTerminal("/operation");
        parserTerm();
        parserOperation();
      break;
      case "<":
        printNonTerminal("operation");
        expectToken(currentToken,"<");
        printNonTerminal("/operation");
        parserTerm();
        parserOperation();
      break;
      case ">":
        printNonTerminal("operation");
        expectToken(currentToken,">");
        printNonTerminal("/operation");
        parserTerm();
        parserOperation();
      break;
      case "|":
        printNonTerminal("operation");
        expectToken(currentToken,"|");
        printNonTerminal("/operation");
        parserTerm();
        parserOperation();
      break;
    }
  }

  void parserExpression(){
    printNonTerminal("expression");
    parserTerm();
    parserOperation();
    printNonTerminal("/expression");
  }

}