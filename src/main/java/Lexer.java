import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Lexer { 
    private String data = "";
    private Map<String, TokenClass> classes = new HashMap<>();

    private int currentPos = 0; //current position in the string

    public Lexer(String filename){
        initialiseClassesMap();

        //read from file into data string
        try {
            File input = new File(filename);
            Scanner myReader = new Scanner(input);

            while (myReader.hasNextLine()) {
                data += myReader.nextLine() + System.lineSeparator();
            }

            System.out.println(data);
            myReader.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    //produces a list of tokens (or an error for invalid input)
    public ArrayList<Token> tokenize(){
        //split input stream into array using newline as delimiter
        String[] inputStreamArray = data.split(System.lineSeparator());
        ArrayList<Token> tokens = new ArrayList<>();
        Token newToken;
        String line;
        
        for(int i=0; i<inputStreamArray.length; i++){
            line = inputStreamArray[i];
            while(currentPos < line.length()){

                // skip whitespace
                if(line.charAt(currentPos) == ' '){
                    currentPos++;
                    continue; 
                }
                //retrieve next token
                newToken = nextToken(line);

                if(newToken != null){
                    tokens.add(newToken);
                }
                else{
                    throw new IllegalArgumentException(
                        "Syntax error on line " +(i+1) + ": \"" + line.charAt(currentPos)+ "\""
                        );
                }
            }
            currentPos = 0;
        }

        return tokens;
    }

    // generates a new token
    private Token nextToken(String line){
        // loop through different token class regex expressions
        // find token which matches the (first) expression 
        for(String regexKey : classes.keySet()){

            // start matching from the beginning of the substring 
            // starting at index currentPos
            Pattern pattern = Pattern.compile("^(" + regexKey + ")");
            Matcher matcher = pattern.matcher(line.substring(currentPos));
            
            if (matcher.find()) {
                String value = matcher.group();
                currentPos += value.length();
                return new Token(classes.get(regexKey), value);
            }
        }
        return null;
        
    }

    private void initialiseClassesMap(){
        classes.put("V_[a-z]([a-z]|[0-9])*", TokenClass.VARIABLE); //Variables
        classes.put("F_[a-z]([a-z]|[0-9])*", TokenClass.USER_DEFINED_FUNCTION); //Functions
        classes.put("\"[A-Z][a-z]{7}\"", TokenClass.TEXT); //Strings
        classes.put("0|" +
               "0\\.([0-9])*[1-9]|" +
               "-0\\.([0-9])*[1-9]|" +
               "[1-9]([0-9])*|" +
               "-[1-9]([0-9])*|" +
               "[1-9]([0-9])*\\.([0-9])*[1-9]|" +
               "-[1-9]([0-9])*\\.([0-9])*[1-9]", TokenClass.NUMBER); //Numbers
        classes.put(String.join("|",KEYWORDS), TokenClass.RESERVED_KEYWORD); //Reserved Keywords

    }

    public static String[] KEYWORDS = {
        "main",                         // Program
        "num", "text",                  // variable type
        "begin", "end",                 // algo
        "skip", "halt", "print",        // command
        ";", "\\(", ",", "\\)",
        "\\{", "\\}",                   // character
        "<input", "=",                  // assignment
        "if", "then", "else",           // conditional-branch
        "not", "sqrt",                  // unary operator
        "or", "and", "eq", "grt",
        "add", "sub", "mul", "div",     // binary operator
        "num", "void",                  // function (?) type
    };

}
