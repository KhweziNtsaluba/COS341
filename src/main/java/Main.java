import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Specify the relative path to the input file
        String inputFile = "src/main/resources/input.txt";
        Lexer lexer = null;

        // Check if an argument is passed for the file location
        if (args.length > 0) {
            inputFile = args[0];
        } else {
            System.out.println("No input file inserted -- using default resources/input.txt");
        }

        performLexing(inputFile, lexer);

        // System.out.println(lexer.tokenize());

        ParseTable pt = new ParseTable();
        pt.displayTablesToFile("parseTableOutput.txt");
        // Parser parser = new Parser(pt);
        // parser.parse();
    }

    static void performLexing(String inputFilePath, Lexer lexer){
        // Print the path to the file being read
        File file = new File(inputFilePath);
        System.out.println("Attempting to read from: " + file.getAbsolutePath());

        // Check if the file exists before proceeding
        if (!file.exists()) {
            System.out.println("File not found: " + file.getAbsolutePath());
            return;
        }

        // Initialize the Lexer with the file path and tokenize
        lexer = new Lexer(inputFilePath);
        List<Token> tokens = lexer.tokenize();

        // Generate XML formatted string from tokens using XMLGenerator
        XMLGenerator xmlGenerator = new XMLGenerator();
        String xmlOutput = xmlGenerator.generateXML(tokens);

        // Output XML to console (optional)
        System.out.println(xmlOutput);

        // Write XML to file
        xmlGenerator.writeXMLToFile(xmlOutput, "tokens.xml");
    }
}

