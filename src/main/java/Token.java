public class Token {
    private TokenClass tokenClass;
    private String tokenWord;
    private int id;
    private static int ID_COUNTER = 0;

    public Token(TokenClass tokenClass, String tokenName){
        this.tokenClass = tokenClass;
        this.tokenWord = tokenName; 
        this.id = ID_COUNTER++;
    }
    
    public TokenClass getTokenClass() {
        return tokenClass;
    }

    public String getTokenWord() {
        return tokenWord;
    }

    public int getId() {
        return id;
    }

    //returns token XML
    public String toString(){
        return "<TOK>\n\n" +
           "<ID>" + id + "</ID>\n\n" +
           "<CLASS>" + tokenClass + "</CLASS> // comment: The class corresponds to some Accept-State of the DFA\n\n" +
           "<WORD>" + tokenWord + "</WORD>\n\n" +
           "</TOK>";
    }
}
