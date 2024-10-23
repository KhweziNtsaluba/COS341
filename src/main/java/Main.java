import java.io.File;
import java.util.List;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    final static String TOKEN_FILE_NAME = "tokens.xml";
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

        ParseTable pt = new ParseTable();
        pt.displayTablesToFile("parseTableOutput.txt");

        performLexing(inputFile, lexer);
        ASTNode parseTree = performParsing(pt);
        SymbolTable symbolTable = performScopeAnalysis(parseTree);

        // System.out.println(lexer.tokenize());

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
        xmlGenerator.writeXMLToFile(xmlOutput, TOKEN_FILE_NAME);
    }
    
    static ASTNode performParsing(ParseTable table){
        // Extract XML tokens
        List<Token> tokens = parseXMLTokens(TOKEN_FILE_NAME);
        tokens.add(new Token(TokenClass.END_OF_INPUT, "$", 39));
        Parser parser = new Parser(table, tokens);
        
        
        // create symbol table
        ASTNode parseTree = parser.parse();
        return parseTree;
    }
    
    private static SymbolTable performScopeAnalysis(ASTNode parseTree) {
        ScopeAnalyser scopeAnalyser = new ScopeAnalyser();
        SymbolTable symbolTable;
        if(parseTree != null){
            try {
                symbolTable = scopeAnalyser.crawl(parseTree);
                return symbolTable;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("SYNTAX ERROR: Parsing failed unexpectedly");
        return null;
    }

    public static List<Token> parseXMLTokens(String filePath) {
        List<Token> tokens = new ArrayList<>();
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            Document document = builder.parse(new File(filePath));
            
            // Normalize XML structure
            document.getDocumentElement().normalize();
            
            // Get all <TOK> elements
            NodeList nodeList = document.getElementsByTagName("TOK");
            
            // Loop through each <TOK> element
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element tokenElement = (Element) node;
                    
                    // Extract ID, CLASS, and WORD from the token element
                    int id = Integer.parseInt(tokenElement.getElementsByTagName("ID").item(0).getTextContent());
                    String className = tokenElement.getElementsByTagName("CLASS").item(0).getTextContent();
                    String word = tokenElement.getElementsByTagName("WORD").item(0).getTextContent();
                    
                    TokenClass tokenClass = mapTokenClass(className);
                    
                    tokens.add(new Token(tokenClass, word, id));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return tokens;
    }
    
    private static TokenClass mapTokenClass(String className) {
        switch (className.toLowerCase()) {
            case "reserved_keyword":
            return TokenClass.RESERVED_KEYWORD;
            case "variable":
                return TokenClass.VARIABLE;
            case "number":
                return TokenClass.NUMBER;
            case "text":
                return TokenClass.TEXT;
            case "user_defined_function":
                return TokenClass.USER_DEFINED_FUNCTION;
            default:
                throw new IllegalArgumentException("Unknown token class: " + className);
        }
    }
}

