public class Main {
 public static void main(String[] args) {
        String inputFile = "../resources/input.txt";
        Lexer lexer;

        if(args.length > 0){
            inputFile = args[0];
        }
        else
            System.out.println("No input file inserted -- using input.txt");
        
        System.out.println("reading from " + inputFile);

        //generate token xml
        // lexer = new Lexer(inputFile);
        // System.out.println(lexer.tokenize());

        ParseTable pt = new ParseTable();
        pt.displayTables();
        // Parser parser = new Parser(pt);
        // parser.parse(inputFile);

// lexer
    }
    
}
