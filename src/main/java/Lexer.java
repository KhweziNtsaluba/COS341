import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Lexer {
    public String data = "";

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

    
}
