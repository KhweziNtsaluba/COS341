import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
public class Lexer {
    private String data = "";
    private Map<String, TokenClass> classes;

    private int currentPos = 0; //current position in the string

    public Lexer(String filename){

        //read from file into data string
        try {
            File input = new File(filename);
            Scanner myReader = new Scanner(input);

            while (myReader.hasNextLine()) {
                data += myReader.nextLine();
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
    List<Token> tokenize(String inputStream){
        throw new UnsupportedOperationException("Not implemented");
    }

    //generates a new token
    private void nextToken(){
        throw new UnsupportedOperationException("Not implemented");
    }

}
