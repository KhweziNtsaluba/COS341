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

    public Token(TokenClass tokenClass, String tokenName, int ID){
        this.tokenClass = tokenClass;
        this.tokenWord = tokenName; 
        this.id = ID;
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
        return 
            "<TOK>"                                 +System.lineSeparator() +
            "<ID>" + id + "</ID>"                   +System.lineSeparator() +
            "<CLASS>" + tokenClass
                .toString()
                .toLowerCase() + "</CLASS>"         +System.lineSeparator() +
            "<WORD>" + tokenWord + "</WORD>"        +System.lineSeparator() +
            "</TOK>"                                +System.lineSeparator()
        ;
    }
}
