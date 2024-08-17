public class Main {
 public static void main(String[] args) {
        String inputFile = "";
        Lexer lexer;
        if(args.length > 0){
            inputFile = args[0];
            System.out.println("reading from" + inputFile);
            lexer = new Lexer(inputFile);
        }
        else
            System.out.println("No input file inserted");
    }
    
}
