class Parser {
  private  Scanner scanner;//léxico é entrada do analisador sintático - chamando por demanda
  private Token currentToken;
  private Token peekToken;
  private StringBuilder xmlOutput = new StringBuilder();  
  
  public Parser (Scanner scanner){
    this.scanner = scanner;
  }

  private void nextToken() {
    currentToken = scanner.nextToken();    
  }


  void start(){
    currentToken = scanner.nextToken();
     //System.out.println(currentToken.getText());
    statements(currentToken);
   
  }
  public void statements(Token currentToken){
    switch(currentToken.getText()){
      case "let":
        parserLet();
      break;
    }
  }

  void parserLet(){
    printNonTerminal("letStatement");
    expectToken(currentToken, "let");
    expectToken(currentToken, "identifier");
    expectToken(currentToken, "=");
    parserTerm();
    expectToken(currentToken, ";");
    printNonTerminal("/letStatement");
  }

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

  private void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
  }

  public String XMLOutput() {
    return xmlOutput.toString();
  }

  void parserTerm(){
    printNonTerminal("term");
    switch(currentToken.getType()){
      case "integerConstant":
        expectToken(currentToken, "integerConstant");
      break;
    }
    printNonTerminal("/term");
  }

}