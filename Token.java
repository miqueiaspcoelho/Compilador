
class Token {
  public static final String keyword = "keyword";
  public static final String symbol = "symbol";
  public static final String identifier = "identifier";
  public static final String intConst = "integerConstant";
  public static final String stringConst = "stringConstant";
  public static final String ilegal = "ilegal";
  
  private String type;
  private String text;

  public Token (String type, String text){
    super();
    this.type = type;
    this.text = text;
  }
  public Token(){
    super();
  }

  public String getType(){
    return type;
  }
  public void setType(String type){
    this.type=type;
  }
  public String getText(){
    return text;
  }
  public void setText(String text){
    this.text = text;
  }

  
  @Override
  public String toString(){
    return "<"+ type +"> " + text + " </"+ type + ">";
  }
  
}