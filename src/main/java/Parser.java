import java.util.*;

public class Parser {
    private ParseTable parseTable;
    private Deque<Integer> stateStack;  // Stack to hold states
    private List<Token> tokens;  // The input token stream
    private Deque<ASTNode> astStack;  // Stack to hold AST nodes

    public Parser(ParseTable parseTable, List<Token> tokens) {
        this.parseTable = parseTable;
        this.tokens = tokens;
        this.stateStack = new ArrayDeque<>();
        this.astStack = new ArrayDeque<>();
        stateStack.push(0);  // Start in state 0
    }

    public boolean parse() {
        int tokenIndex = 0;
    
        while (tokenIndex < tokens.size()) {
            Token currentToken = tokens.get(tokenIndex);
            System.out.println(
                stateStack + " | "
                + currentToken.getTokenWord() + " | "
            );
            int currentState = stateStack.peek();
    
            // Get the action for the current state and input token
            ParseTable.Action action = parseTable.getAction(currentState, currentToken);
    
            if (action == null) {
                System.out.println("Syntax error at token: " + currentToken);
                return false;  // Syntax error
            }
    
            switch (action.getType()) {
                case SHIFT:
                    // Shift operation: push the new state and advance input
                    stateStack.push(action.getNumber());
                    tokenIndex++;  // Move to the next token
    
                    // Create an AST node for the token and push it onto the AST stack
                    ASTNode tokenNode = new LeafTreeNode(currentToken);
                    astStack.push(tokenNode);
                    break;
    
                case REDUCE:
                    // Reduce operation: pop the stack according to production length
                    int productionLength = getProductionLength(action.getNumber());
                    if (astStack.size() < productionLength || stateStack.size() < productionLength) {
                        System.out.println("Syntax error: insufficient elements for reduction.");
                        return false;  // Not enough elements to reduce
                    }
    
                    ASTNode parentNode = new InternalTreeNode(getNonTerminal(action.getNumber()));  // Create a new AST node
    
                    // Pop the right number of AST nodes and add them as children of the parent node
                    for (int i = 0; i < productionLength; i++) {
                        parentNode.addChild(astStack.pop());
                        stateStack.pop();
                    }
    
                    // Push the newly created parent node onto the AST stack
                    astStack.push(parentNode);
    
                    // Use goto table to transition after the reduction
                    int nextState = parseTable.getGoto(stateStack.peek(), getNonTerminal(action.getNumber()));
                    stateStack.push(nextState);
    
                    System.out.println("Reduced using production " + action.getNumber());
                    break;
    
                case ACCEPT:
                    System.out.println("Input successfully parsed!");
                    System.out.println("AST: " + astStack.peek());  // Print the final AST
                    return true;
    
                case ERROR:
                default:
                    System.out.println("Syntax error at token: " + currentToken);
                    return false;
            }
        }
    
        System.out.println("Syntax error: Unexpected end of input.");
        return false;
    }
    
    // Helper method to return the length of the production rule (this is based on your grammar)
    private int getProductionLength(int productionNumber) {
        switch (productionNumber) {
            case 1: return 4;  // PROG -> main GLOBVARS ALGO FUNCTIONS
            case 2: return 0;  // GLOBVARS -> ''
            case 3: return 3;  // GLOBVARS -> VTYP VNAME , GLOBVARS
            case 4: return 1;  // VTYP -> num
            case 5: return 1;  // VTYP -> text
            case 6: return 1;  // VNAME -> V
            case 7: return 3;  // ALGO -> begin INSTRUC end
            case 8: return 0;  // INSTRUC -> ''
            case 9: return 2;  // INSTRUC -> COMMAND ; INSTRUC
            case 10: return 1; // COMMAND -> skip
            case 11: return 1; // COMMAND -> halt
            case 12: return 2; // COMMAND -> print ATOMIC
            case 13: return 1; // COMMAND -> ASSIGN
            case 14: return 1; // COMMAND -> CALL
            case 15: return 1; // COMMAND -> BRANCH
            case 16: return 2; // COMMAND -> return ATOMIC
            case 17: return 1; // ATOMIC -> VNAME
            case 18: return 1; // ATOMIC -> CONST
            case 19: return 1; // CONST -> N
            case 20: return 1; // CONST -> T
            case 21: return 2; // ASSIGN -> VNAME < input
            case 22: return 3; // ASSIGN -> VNAME = TERM
            case 23: return 4; // CALL -> FNAME ( ATOMIC , ATOMIC , ATOMIC )
            case 24: return 4; // BRANCH -> if COND then ALGO else ALGO
            case 25: return 1; // TERM -> ATOMIC
            case 26: return 1; // TERM -> CALL
            case 27: return 1; // TERM -> OP
            case 28: return 3; // OP -> UNOP ( ARG )
            case 29: return 4; // OP -> BINOP( ARG , ARG )
            case 30: return 1; // ARG -> ATOMIC
            case 31: return 1; // ARG -> OP
            case 32: return 1; // COND -> SIMPLE
            case 33: return 1; // COND -> COMPOSIT
            case 34: return 3; // SIMPLE -> BINOP ( ATOMIC , ATOMIC )
            case 35: return 2; // COMPOSIT -> BINOP( SIMPLE , SIMPLE )
            case 36: return 2; // COMPOSIT -> UNOP ( SIMPLE )
            case 37: return 1; // UNOP -> not
            case 38: return 1; // UNOP -> sqrt
            case 39: return 1; // BINOP -> or
            case 40: return 1; // BINOP -> and
            case 41: return 1; // BINOP -> eq
            case 42: return 1; // BINOP -> grt
            case 43: return 1; // BINOP -> add
            case 44: return 1; // BINOP -> sub
            case 45: return 1; // BINOP -> mul
            case 46: return 1; // BINOP -> div
            case 47: return 1; // FNAME -> F
            case 48: return 0;  // FUNCTIONS -> ''
            case 49: return 2;  // FUNCTIONS -> DECL FUNCTIONS
            case 50: return 2;  // DECL -> HEADER BODY
            case 51: return 4;  // HEADER -> FTYP FNAME ( VNAME , VNAME , VNAME )
            case 52: return 1;  // FTYP -> num
            case 53: return 1;  // FTYP -> void
            case 54: return 4;  // BODY -> PROLOG LOCVARS ALGO EPILOG SUBFUNCS end
            case 55: return 1;  // PROLOG -> {
            case 56: return 1;  // EPILOG -> }
            case 57: return 3;  // LOCVARS -> VTYP VNAME , VTYP VNAME , VTYP VNAME ,
            case 58: return 1;  // SUBFUNCS -> FUNCTIONS
            default: return 1;  // Default length if unknown
        }
    }
    
    
    // Helper method to get the non-terminal symbol for a given production rule
    private String getNonTerminal(int productionNumber) {
        switch (productionNumber) {
            case 1: return "PROG";  
            case 2: return "GLOBVARS";  
            case 3: return "GLOBVARS";  
            case 4: return "ALGO";  
            case 5: return "INSTRUC";  
            case 6: return "COMMAND";  
            case 7: return "COMMAND";  
            case 8: return "COMMAND";  
            case 9: return "ASSIGN";  
            case 10: return "CALL";  
            case 11: return "BRANCH";  
            case 12: return "ATOMIC";  
            case 13: return "CONST";  
            case 14: return "TERM";  
            case 15: return "OP";  
            case 16: return "ARG";  
            case 17: return "COND";  
            case 18: return "SIMPLE";  
            case 19: return "COMPOSIT";  
            case 20: return "UNOP";  
            case 21: return "FNAME";  
            case 22: return "FUNCTIONS";  
            case 23: return "DECL";  
            case 24: return "HEADER";  
            case 25: return "BODY";  
            case 26: return "PROLOG";  
            case 27: return "EPILOG";  
            case 28: return "LOCVARS";  
            case 29: return "SUBFUNCS";  
            default: return "S";  // Default non-terminal if unknown
        }
    }
    public static void main(String[] args) {
        ParseTable table = new ParseTable();
        
        List<Token> tokens = Arrays.asList(
            new Token(TokenClass.RESERVED_KEYWORD, "main", 0),               // Start of the program
            new Token(TokenClass.RESERVED_KEYWORD, "num", 1),                // Variable type for the first global variable (VTYP)
            new Token(TokenClass.VARIABLE, "V_somevar", 2),                  // First variable name (VNAME)
            new Token(TokenClass.RESERVED_KEYWORD, ",", 3),                  // Comma to separate variable declarations
            new Token(TokenClass.RESERVED_KEYWORD, "text", 4),               // Variable type for the second global variable (VTYP)
            new Token(TokenClass.VARIABLE, "V_Hellothe", 5),                 // Second variable name (VNAME)
            new Token(TokenClass.RESERVED_KEYWORD, "begin", 6),              // Start of the algorithm (ALGO)
            new Token(TokenClass.RESERVED_KEYWORD, "if", 7),                 // Start of a conditional statement (BRANCH)
            new Token(TokenClass.RESERVED_KEYWORD, "(", 8),                  // Opening parenthesis for condition
            new Token(TokenClass.VARIABLE, "V_var1", 9),                     // First variable in condition (ATOMIC)
            new Token(TokenClass.RESERVED_KEYWORD, "grt", 10),               // Greater than operator (BINOP)
            new Token(TokenClass.NUMBER, "10", 11),                          // Constant in condition (CONST)
            new Token(TokenClass.RESERVED_KEYWORD, ")", 12),                 // Closing parenthesis for condition
            new Token(TokenClass.RESERVED_KEYWORD, "{", 13),                 // Opening brace for the block
            new Token(TokenClass.VARIABLE, "V_var2", 14),                    // Variable on the left side of an assignment (VNAME)
            new Token(TokenClass.RESERVED_KEYWORD, "=", 15),                 // Assignment operator
            new Token(TokenClass.VARIABLE, "V_var1", 16),                    // Variable being assigned (ATOMIC)
            new Token(TokenClass.RESERVED_KEYWORD, "add", 17),               // Addition operator
            new Token(TokenClass.NUMBER, "5", 18),                           // Constant to add (CONST)
            new Token(TokenClass.RESERVED_KEYWORD, ";", 19),                 // End of the statement
            new Token(TokenClass.RESERVED_KEYWORD, "}", 20),                 // Closing brace for the block
            new Token(TokenClass.RESERVED_KEYWORD, "end", 21),               // End of the algorithm (ALGO)
            new Token(TokenClass.END_OF_INPUT, "$", 22)                      // End of input
        );

        Parser parser = new Parser(table, tokens);
        parser.parse();
    }
}
