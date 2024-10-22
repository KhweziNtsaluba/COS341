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
        // Array where index corresponds to productionNumber, and value is the length of RHS of the production
        int[] productionLengths = new int[] {
            4,  // PROG -> main GLOBVARS ALGO FUNCTIONS
            0,  // GLOBVARS -> ''
            4,  // GLOBVARS -> VTYP VNAME , GLOBVARS
            1,  // VTYP -> num
            1,  // VTYP -> text
            1,  // VNAME -> V
            3,  // ALGO -> begin INSTRUC end
            0,  // INSTRUC -> ''
            3,  // INSTRUC -> COMMAND ; INSTRUC
            1,  // COMMAND -> skip
            1,  // COMMAND -> halt
            2,  // COMMAND -> print ATOMIC
            1,  // COMMAND -> ASSIGN
            1,  // COMMAND -> CALL
            1,  // COMMAND -> BRANCH
            1,  // ATOMIC -> VNAME
            1,  // ATOMIC -> CONST
            1,  // CONST -> N
            1,  // CONST -> T
            3,  // ASSIGN -> VNAME < input
            3,  // ASSIGN -> VNAME = TERM
            6,  // CALL -> FNAME ( ATOMIC , ATOMIC , ATOMIC )
            7,  // BRANCH -> if COND then ALGO else ALGO
            1,  // TERM -> ATOMIC
            1,  // TERM -> CALL
            1,  // TERM -> OP
            3,  // OP -> UNOP ( ARG )
            5,  // OP -> BINOP ( ARG , ARG )
            1,  // ARG -> ATOMIC
            1,  // ARG -> OP
            1,  // COND -> SIMPLE
            1,  // COND -> COMPOSIT
            5,  // SIMPLE -> BINOP ( ATOMIC , ATOMIC )
            5,  // COMPOSIT -> BINOP ( SIMPLE , SIMPLE )
            3,  // COMPOSIT -> UNOP ( SIMPLE )
            1,  // UNOP -> not
            1,  // UNOP -> sqrt
            1,  // BINOP -> or
            1,  // BINOP -> and
            1,  // BINOP -> eq
            1,  // BINOP -> grt
            1,  // BINOP -> add
            1,  // BINOP -> sub
            1,  // BINOP -> mul
            1,  // BINOP -> div
            1,  // FNAME -> F
            0,  // FUNCTIONS -> ''
            2,  // FUNCTIONS -> DECL FUNCTIONS
            2,  // DECL -> HEADER BODY
            6,  // HEADER -> FTYP FNAME ( VNAME , VNAME , VNAME )
            1,  // FTYP -> num
            1,  // FTYP -> void
            6,  // BODY -> PROLOG LOCVARS ALGO EPILOG SUBFUNCS end
            1,  // PROLOG -> {
            1,  // EPILOG -> }
            7,  // LOCVARS -> VTYP VNAME , VTYP VNAME , VTYP VNAME ,
            1,  // SUBFUNCS -> FUNCTIONS
            2   // COMMAND -> return ATOMIC
        };
    
        // Ensure the productionNumber is within bounds
        if (productionNumber >= 0 && productionNumber < productionLengths.length) {
            return productionLengths[productionNumber];
        } else {
            throw new IllegalArgumentException("Invalid production number: " + productionNumber);
        }
    }
    
    
    
    // Helper method to get the non-terminal symbol for a given production rule
    private String getNonTerminal(int productionNumber) {
        // Array where index corresponds to productionNumber, and value is the non-terminal of the production
        String[] nonTerminals = new String[] {
            "PROG",         // PROG -> main GLOBVARS ALGO FUNCTIONS
            "GLOBVARS",     // GLOBVARS -> ''
            "GLOBVARS",     // GLOBVARS -> VTYP VNAME , GLOBVARS
            "VTYP",         // VTYP -> num
            "VTYP",         // VTYP -> text
            "VNAME",        // VNAME -> V
            "ALGO",         // ALGO -> begin INSTRUC end
            "INSTRUC",      // INSTRUC -> ''
            "INSTRUC",      // INSTRUC -> COMMAND ; INSTRUC
            "COMMAND",      // COMMAND -> skip
            "COMMAND",      // COMMAND -> halt
            "COMMAND",      // COMMAND -> print ATOMIC
            "COMMAND",      // COMMAND -> ASSIGN
            "COMMAND",      // COMMAND -> CALL
            "COMMAND",      // COMMAND -> BRANCH
            "ATOMIC",       // ATOMIC -> VNAME
            "ATOMIC",       // ATOMIC -> CONST
            "CONST",        // CONST -> N
            "CONST",        // CONST -> T
            "ASSIGN",       // ASSIGN -> VNAME < input
            "ASSIGN",       // ASSIGN -> VNAME = TERM
            "CALL",         // CALL -> FNAME ( ATOMIC , ATOMIC , ATOMIC )
            "BRANCH",       // BRANCH -> if COND then ALGO else ALGO
            "TERM",         // TERM -> ATOMIC
            "TERM",         // TERM -> CALL
            "TERM",         // TERM -> OP
            "OP",           // OP -> UNOP ( ARG )
            "OP",           // OP -> BINOP ( ARG , ARG )
            "ARG",          // ARG -> ATOMIC
            "ARG",          // ARG -> OP
            "COND",         // COND -> SIMPLE
            "COND",         // COND -> COMPOSIT
            "SIMPLE",       // SIMPLE -> BINOP ( ATOMIC , ATOMIC )
            "COMPOSIT",     // COMPOSIT -> BINOP ( SIMPLE , SIMPLE )
            "COMPOSIT",     // COMPOSIT -> UNOP ( SIMPLE )
            "UNOP",         // UNOP -> not
            "UNOP",         // UNOP -> sqrt
            "BINOP",        // BINOP -> or
            "BINOP",        // BINOP -> and
            "BINOP",        // BINOP -> eq
            "BINOP",        // BINOP -> grt
            "BINOP",        // BINOP -> add
            "BINOP",        // BINOP -> sub
            "BINOP",        // BINOP -> mul
            "BINOP",        // BINOP -> div
            "FNAME",        // FNAME -> F
            "FUNCTIONS",    // FUNCTIONS -> ''
            "FUNCTIONS",    // FUNCTIONS -> DECL FUNCTIONS
            "DECL",         // DECL -> HEADER BODY
            "HEADER",       // HEADER -> FTYP FNAME ( VNAME , VNAME , VNAME )
            "FTYP",         // FTYP -> num
            "FTYP",         // FTYP -> void
            "BODY",         // BODY -> PROLOG LOCVARS ALGO EPILOG SUBFUNCS end
            "PROLOG",       // PROLOG -> { 
            "EPILOG",       // EPILOG -> }
            "LOCVARS",      // LOCVARS -> VTYP VNAME , VTYP VNAME , VTYP VNAME ,
            "SUBFUNCS",     // SUBFUNCS -> FUNCTIONS
            "COMMAND"       // COMMAND -> return ATOMIC
        };

        // Ensure the productionNumber is within bounds
        if (productionNumber >= 0 && productionNumber < nonTerminals.length) {
            return nonTerminals[productionNumber];
        } else {
            throw new IllegalArgumentException("Invalid production number: " + productionNumber);
        }
    }

    public static void main(String[] args) {
        ParseTable table = new ParseTable();
        
        List<Token> tokens = Arrays.asList(
    new Token(TokenClass.RESERVED_KEYWORD, "main", 0),                   // 0: Start of the program
    new Token(TokenClass.RESERVED_KEYWORD, "num", 1),                       // 1: Variable type for the first global variable (VTYP)
    new Token(TokenClass.VARIABLE, "V_somevar", 2),                      // 2: First variable name (VNAME)
    new Token(TokenClass.RESERVED_KEYWORD, ",", 3),                       // 3: Comma to separate variable declarations
    new Token(TokenClass.RESERVED_KEYWORD, "text", 4),                      // 4: Variable type for the second global variable (VTYP)
    new Token(TokenClass.VARIABLE, "V_hellothe", 5),                      // 5: Second variable name (VNAME)
    new Token(TokenClass.RESERVED_KEYWORD, ",", 6),                       // 6: Comma to separate variable declarations
    new Token(TokenClass.RESERVED_KEYWORD, "begin", 7),                   // 7: Start of the algorithm (ALGO)
    new Token(TokenClass.VARIABLE, "V_var1", 8),                         // 8: Variable in assignment (VNAME)
    new Token(TokenClass.RESERVED_KEYWORD, "=", 9),                       // 9: Assignment operator
    new Token(TokenClass.NUMBER, "5", 10),                                // 10: Constant in assignment (CONST)
    new Token(TokenClass.RESERVED_KEYWORD, ";", 11),                      // 11: End of the statement
    new Token(TokenClass.RESERVED_KEYWORD, "if", 12),                     // 12: Start of a conditional statement (BRANCH)
    new Token(TokenClass.RESERVED_KEYWORD, "grt", 13),                               // 13: Greater than operator (BINOP)
    new Token(TokenClass.RESERVED_KEYWORD, "(", 14),                      // 14: Opening parenthesis for condition
    new Token(TokenClass.VARIABLE, "V_var1", 15),                         // 15: Variable in condition (ATOMIC)
    new Token(TokenClass.RESERVED_KEYWORD, ",", 16),                      // 16: Comma separating arguments
    new Token(TokenClass.NUMBER, "10", 17),                               // 17: Constant in condition (CONST)
    new Token(TokenClass.RESERVED_KEYWORD, ")", 18),                      // 18: Closing parenthesis for condition
    new Token(TokenClass.RESERVED_KEYWORD, "then", 19),                   // 19: 'then' keyword in conditional
    new Token(TokenClass.RESERVED_KEYWORD, "begin", 20),                  // 20: Start of the inner algorithm block
    new Token(TokenClass.RESERVED_KEYWORD, "print", 21),                  // 21: Print command (COMMAND)
    new Token(TokenClass.VARIABLE, "V_var1", 22),                         // 22: Variable being printed (ATOMIC)
    new Token(TokenClass.RESERVED_KEYWORD, ";", 23),                      // 23: End of the print statement
    new Token(TokenClass.RESERVED_KEYWORD, "end", 24),                    // 24: End of the inner algorithm block
    new Token(TokenClass.RESERVED_KEYWORD, "else", 25),                   // 25: 'else' keyword in conditional
    new Token(TokenClass.RESERVED_KEYWORD, "begin", 26),                  // 26: Start of the second inner algorithm block
    new Token(TokenClass.VARIABLE, "V_var1", 27),                         // 27: Variable in assignment (VNAME)
    new Token(TokenClass.RESERVED_KEYWORD, "=", 28),                      // 28: Assignment operator
    new Token(TokenClass.RESERVED_KEYWORD, "add", 29),                            // 29: Function call (FUNCTION)
    new Token(TokenClass.RESERVED_KEYWORD, "(", 30),                      // 30: Opening parenthesis for function arguments
    new Token(TokenClass.VARIABLE, "V_var1", 31),                         // 31: First argument (ATOMIC)
    new Token(TokenClass.RESERVED_KEYWORD, ",", 32),                      // 32: Comma separating arguments
    new Token(TokenClass.NUMBER, "5", 33),                                // 33: Second argument (CONST)
    new Token(TokenClass.RESERVED_KEYWORD, ")", 34),                      // 34: Closing parenthesis for function arguments
    new Token(TokenClass.RESERVED_KEYWORD, ";", 35),                      // 35: End of the assignment statement
    new Token(TokenClass.RESERVED_KEYWORD, "end", 36),                    // 36: End of the second inner algorithm block
    new Token(TokenClass.RESERVED_KEYWORD, ";", 37),                      // 37: End of the conditional statement
    new Token(TokenClass.RESERVED_KEYWORD, "end", 38)                     // 38: End of the algorithm (ALGO)
);
       

        Parser parser = new Parser(table, tokens);
        parser.parse();
    }
}
