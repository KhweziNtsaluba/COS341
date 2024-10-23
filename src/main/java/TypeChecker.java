import java.util.HashMap;
import java.util.Map;

public class TypeChecker {

    private Map<String, String> symbolTable = new HashMap<>();

    public boolean checkProgram(ASTNode prog) {
        if (prog == null || prog.getChildren().size() < 3) {
            System.out.println("Error: Program structure is incomplete.");
            return false;
        }

        ASTNode globVars = prog.getChildren().get(0);
        ASTNode algo = prog.getChildren().get(1);
        ASTNode functions = prog.getChildren().get(2);

        return checkGlobalVars(globVars) && checkAlgo(algo) && checkFunctions(functions);
    }

    private boolean checkGlobalVars(ASTNode globVars) {
        if (globVars == null)
            return true;

        for (ASTNode varDecl : globVars.getChildren()) {
            if (varDecl.getChildren().size() < 2) {
                System.out.println("Error: Incomplete variable declaration.");
                return false;
            }

            String varType = typeof(varDecl.getChildren().get(0));
            String varName = varDecl.getChildren().get(1).getValue();

            if (varType == null || varName == null) {
                System.out.println("Error: Invalid variable type or name.");
                return false;
            }

            if (symbolTable.containsKey(varName)) {
                System.out.println("Error: Variable " + varName + " already declared.");
                return false;
            }

            symbolTable.put(varName, varType);
        }
        return true;
    }

    private boolean checkAlgo(ASTNode algo) {
        if (algo == null || algo.getChildren().isEmpty()) {
            System.out.println("Error: Missing algorithm body.");
            return false;
        }

        ASTNode instruc = algo.getChildren().get(0);
        return checkInstruction(instruc);
    }

    private boolean checkInstruction(ASTNode instruc) {
        if (instruc == null || instruc.getValue() == null) {
            System.out.println("Error: Invalid instruction.");
            return false;
        }

        String commandType = instruc.getValue();

        switch (commandType) {
            case "skip":
            case "halt":
                return true;

            case "print": {
                String termType = typeof(instruc.getChildren().get(0));
                if (!termType.equals("n") && !termType.equals("t")) {
                    System.out.println("Error: Only numeric or text can be printed.");
                    return false;
                }
                return true;
            }

            case "ASSIGN": {
                String varName = instruc.getChildren().get(0).getValue(); // Variable name
                String assignmentType = instruc.getChildren().get(1).getValue(); // Type of assignment (input or TERM)

                // Check if it's a user input assignment
                if (assignmentType.equals("<")) {
                    // Assuming we have some mechanism to check for numeric user inputs
                    String varType = symbolTable.get(varName); // Get variable type from symbol table

                    if (varType == null) {
                        System.out.println("Error: Variable " + varName + " not declared.");
                        return false;
                    }

                    // Check if the variable is numeric
                    if (varType.equals("n")) {
                        // Numeric user input is allowed
                        return true; // typecheck(ASSIGN) = true
                    } else {
                        // If variable is not numeric
                        System.out.println("Error: Variable " + varName + " must be numeric for user input.");
                        return false; // typecheck(ASSIGN) = false
                    }
                } else {
                    // Handle assignment of a TERM (which can be either text or numbers)
                    String termType = typeof(instruc.getChildren().get(1)); // Get type of the assigned term
                    String varType = symbolTable.get(varName); // Check variable type from symbol table

                    if (varType == null) {
                        System.out.println("Error: Variable " + varName + " not declared.");
                        return false; // Variable not declared
                    }

                    if (!varType.equals(termType)) {
                        System.out.println("Error: Type mismatch in assignment to " + varName);
                        return false; // Type mismatch
                    }

                    return true; // typecheck(ASSIGN) = true
                }
            }

            case "branch": {
                String condType = typeof(instruc.getChildren().get(0)); // Condition must be checked
                if (!condType.equals("b")) {
                    System.out.println("Error: Condition must be boolean.");
                    return false;
                }

                return checkInstruction(instruc.getChildren().get(1)) && checkInstruction(instruc.getChildren().get(2));
            }

            case "return": {
                // Get the function scope node (FTYP) in the tree where this return belongs
                ASTNode functionScope = findFunctionScope(instruc);
                if (functionScope == null) {
                    System.out.println("Error: 'return' must be inside a function scope.");
                    return false;
                }

                String expectedReturnType = typeof(functionScope); // The type of the function
                String actualReturnType = typeof(instruc.getChildren().get(0)); // The type of the returned atomic value

                if (!expectedReturnType.equals("n")) {
                    System.out.println("Error: Functions can only return numeric values.");
                    return false;
                }

                if (!expectedReturnType.equals(actualReturnType)) {
                    System.out.println("Error: Return type mismatch, expected " + expectedReturnType + " but got "
                            + actualReturnType);
                    return false;
                }
                return true;
            }

            case "CALL": {
                String callType = typeofCall(instruc.getChildren().get(0)); // Assuming CALL refers to a function call
                if (!callType.equals("v")) { // 'v' stands for void-type
                    System.out.println("Error: CALL must return a void type.");
                    return false;
                }
                return true;
            }

            default:
                System.out.println("Error: Unrecognized command " + commandType);
                return false;
        }
    }

    /**
     * This method simulates a "tree crawler" that finds the function type node
     * (FTYP)
     * in which the given `instruc` (return statement) is located. Assumes scope
     * analysis was done.
     */
    private ASTNode findFunctionScope(ASTNode instruc) {
        // Traverse upwards in the tree to find the function scope node (FTYP)
        ASTNode current = instruc.getParent(); // Assuming each node has a parent reference
        while (current != null) {
            if ("FTYP".equals(current.getValue())) {
                return current;
            }
            current = current.getParent();
        }
        return null; // If no function scope was found
    }

    private boolean checkFunctions(ASTNode functions) {
        if (functions == null || functions.getChildren().isEmpty())
            return true;

        for (ASTNode function : functions.getChildren()) {
            if (!checkFunction(function)) {
                System.out.println("Error: Function type check failed.");
                return false;
            }
        }
        return true;
    }

    private boolean checkFunction(ASTNode functionDecl) {
        if (functionDecl == null || functionDecl.getChildren().size() < 2) {
            System.out.println("Error: Invalid function declaration.");
            return false;
        }

        ASTNode header = functionDecl.getChildren().get(0);
        ASTNode body = functionDecl.getChildren().get(1);

        return checkHeader(header) && checkBody(body);
    }

    private boolean checkHeader(ASTNode header) {
        String returnType = typeof(header.getChildren().get(0));
        String funcName = header.getValue();

        if (returnType == null || funcName == null) {
            System.out.println("Error: Function header has invalid return type or name.");
            return false;
        }

        symbolTable.put(funcName, returnType);

        for (ASTNode param : header.getChildren().subList(2, header.getChildren().size())) {
            if (!typeof(param).equals("n")) {
                System.out.println("Error: Function parameters must be numeric.");
                return false;
            }
        }

        return true;
    }

    private boolean checkBody(ASTNode body) {
        if (body == null || body.getChildren().isEmpty()) {
            System.out.println("Error: Function body is empty.");
            return false;
        }
        return checkAlgo(body.getChildren().get(0));
    }

    private String typeof(ASTNode node) {
        if (node == null) {
            System.out.println("Warning: Node is null.");
            return null;
        }

        // Check if the node is a LeafNode
        if (node instanceof LeafTreeNode) {
            Token token = ((LeafTreeNode) node).getToken(); // Assuming LeafTreeNode has a getToken method
            if (token == null) {
                System.out.println("Warning: Leaf node has no token.");
                return null;
            }

            // Use the token class to determine the type
            switch (token.getTokenClass()) {
                case VARIABLE:
                    // Retrieve variable type from symbol table
                    String varType = symbolTable.get(token.getTokenWord());
                    if (varType == null) {
                        System.out.println("Error: Variable " + token.getTokenWord() + " is not declared.");
                        return null;
                    }
                    return varType; // Return the variable's type from the symbol table

                case NUMBER:
                    return "n"; // Numeric type

                case RESERVED_KEYWORD:
                    return switch (token.getTokenWord()) {
                        case "num" -> "n"; // Numeric type
                        case "text" -> "t"; // Text type
                        default -> null; // Unrecognized reserved keywords
                    };

                case CONST:
                    // Handle constants as numeric or text
                    return typeofConst(token.getTokenWord());

                default:
                    System.out.println("Warning: Unrecognized token class " + token.getTokenClass());
                    return null;
            }
        }

        String nodeType = node.getValue();
        if (nodeType == null) {
            System.out.println("Warning: Node has no value.");
            return null;
        }

        // Handle complex nodes such as binary operations, function calls, etc.
        switch (nodeType) {
            case "Var":
                return symbolTable.get(node.getChildren().get(0).getValue()); // Look up the variable type from symbol
                                                                              // table
            case "Const":
                return typeofConst(node.getChildren().get(0).getValue()); // Handle constant values
            case "true":
            case "false":
                return "b"; // Boolean literal
            case "Term":
            case "factor":
                return typeof(node.getChildren().get(0)); // Recursive call for the child
            case "BINOP":
                return typeofBinop(node); // Handle binary operations
            default:
                System.out.println("Warning: Unrecognized type " + nodeType);
                return null;
        }
    }

    private String typeofConst(String value) {
        // Determine type based on the value pattern
        if (value.matches("[0-9]+")) {
            return "n"; // Numeric constant
        } else if (value.matches("\".*\"")) {
            return "t"; // Text constant (assuming text is enclosed in quotes)
        } else {
            System.out.println("Warning: Unrecognized constant value " + value);
            return null;
        }
    }

    private String typeofBinop(ASTNode node) {
        if (node == null || node.getChildren().isEmpty()) {
            System.out.println("Warning: BINOP node is null or has no children.");
            return null;
        }

        // Assuming the first child is the operation token
        String operation = node.getChildren().get(0).getValue(); // Get the operation type

        return switch (operation) {
            case "or", "and" -> "b"; // Boolean type
            case "eq", "grt" -> "c"; // Comparison type
            case "add", "sub", "mul", "div" -> "n"; // Numeric type
            default -> {
                System.out.println("Warning: Unrecognized BINOP operation " + operation);
                yield null; // Return undefined for unrecognized operations
            }
        };
    }

    private String typeofCall(ASTNode node) {
        // Assuming the CALL node has the structure CALL ::= FNAME( ATOMIC1, ATOMIC2,
        // ATOMIC3 )

        if (node.getChildren().size() < 2) {
            return "u"; // Undefined if no parameters are passed
        }

        String funcName = node.getChildren().get(0).getValue(); // Get the function name
        ASTNode params = node.getChildren().get(1); // Get the parameters node

        // Validate the number of parameters
        if (params.getChildren().size() != 3) {
            return "u"; // Undefined if not exactly three parameters
        }

        // Get the types of the parameters
        String type1 = typeof(params.getChildren().get(0)); // ATOMIC1
        String type2 = typeof(params.getChildren().get(1)); // ATOMIC2
        String type3 = typeof(params.getChildren().get(2)); // ATOMIC3

        // Check if all three parameters are numeric
        boolean allNumeric = type1.equals("n") && type2.equals("n") && type3.equals("n");

        if (allNumeric) {
            // Consult the symbol table for the function's return type
            String returnType = symbolTable.get(funcName); // Assuming this returns the expected return type of the
                                                           // function
            if (returnType != null) {
                return returnType; // Return the type of the function
            }
        }

        return "u"; // Return 'u' if not all parameters are numeric or function not found
    }

    private boolean checkOp(ASTNode opNode) {
        if (opNode == null || opNode.getChildren().isEmpty()) {
            System.out.println("Error: Incomplete operation.");
            return false;
        }

        String operationType = opNode.getChildren().get(0).getValue(); // The type of operation (UNOP/BINOP)
        String argType = typeof(opNode.getChildren().get(1)); // The type of the argument (for unary operations)

        // Handling UNOP
        if (operationType.equals("UNOP")) {
            String unop = opNode.getChildren().get(0).getValue(); // Get the actual unary operation

            // Determine the argument type
            String arg = typeof(opNode.getChildren().get(1));

            // Check the argument type based on the unary operation
            switch (unop) {
                case "not":
                    if (arg.equals("b")) {
                        // Both UNOP and ARG are boolean
                        return true; // Operation type is boolean
                    } else {
                        System.out.println("Error: Argument for 'not' must be boolean.");
                        return false;
                    }

                case "sqrt":
                    if (arg.equals("n")) {
                        // Both UNOP and ARG are numeric
                        return true; // Operation type is numeric
                    } else {
                        System.out.println("Error: Argument for 'sqrt' must be numeric.");
                        return false;
                    }

                default:
                    System.out.println("Error: Unrecognized unary operation " + unop);
                    return false;
            }
        }

        // Handling BINOP
        else if (operationType.equals("BINOP")) {
            // Assuming we have two arguments for binary operations
            String arg1Type = typeof(opNode.getChildren().get(1));
            String arg2Type = typeof(opNode.getChildren().get(2));

            if (arg1Type.equals("b") && arg2Type.equals("b")) {
                return true; // Both arguments are boolean
            } else if (arg1Type.equals("n") && arg2Type.equals("n")) {
                return true; // Both arguments are numeric
            } else if (arg1Type.equals("n") && arg2Type.equals("n")) {
                return true; // Comparison operation
            } else {
                System.out.println("Error: Incompatible argument types for binary operation.");
                return false;
            }
        }

        System.out.println("Error: Unrecognized operation type " + operationType);
        return false; // If operation type is not recognized
    }

    private boolean typecheckFunctions(ASTNode functionsNode) {
        if (functionsNode == null) {
            return true; // Base case of the type-checking recursion
        }
    
        for (ASTNode function : functionsNode.getChildren()) {
            if (!typecheckFunctions1(function)) {
                return false; // If any function fails type check, return false
            }
        }
        return true; // All functions passed type check
    }
    
    private boolean typecheckFunctions1(ASTNode functions1Node) {
        if (functions1Node.getChildren().size() < 2) {
            System.out.println("Error: Incomplete FUNCTIONS1 operation.");
            return false; // Incomplete operation
        }
    
        ASTNode declNode = functions1Node.getChildren().get(0); // DECL node
        ASTNode functions2Node = functions1Node.getChildren().get(1); // FUNCTIONS2 node
    
        return typecheckDecl(declNode) && typecheckFunctions(functions2Node);
    }
    
    private boolean typecheckDecl(ASTNode declNode) {
        if (declNode.getChildren().size() < 2) {
            System.out.println("Error: Incomplete DECL operation.");
            return false; // Incomplete operation
        }
    
        ASTNode headerNode = declNode.getChildren().get(0); // HEADER node
        ASTNode bodyNode = declNode.getChildren().get(1); // BODY node
    
        return typecheckHeader(headerNode) && typecheckBody(bodyNode);
    }
    
    private boolean typecheckHeader(ASTNode headerNode) {
        if (headerNode.getChildren().size() < 4) {
            System.out.println("Error: Incomplete HEADER operation.");
            return false; // Incomplete operation
        }
    
        String ftyp = typeof(headerNode.getChildren().get(0)); // FTYP
        ASTNode fnameNode = headerNode.getChildren().get(1); // FNAME as ASTNode
        String vname1Type = typeof(headerNode.getChildren().get(2)); // VNAME1
        String vname2Type = typeof(headerNode.getChildren().get(3)); // VNAME2
    
        // Accessing the function name's type
        String fnameType = typeof(fnameNode); // Get the type of FNAME from the ASTNode
    
        // Add to symbol table
        symbolTable.put(ftyp, fnameNode.getValue()); // Link the function type to the function name
    
        // Check function return type against header type
        if (!ftyp.equals(fnameType)) {
            System.out.println("Error: Function return type does not match header type.");
            return false;
        }
    
        // Check if all variable names are of numeric type
        if (vname1Type.equals("n") && vname2Type.equals("n")) {
            return true; // Valid numeric arguments
        }
    
        return false; // Invalid argument types
    }
    
    // Placeholder for body type-checking
    private boolean typecheckBody(ASTNode bodyNode) {
        // Implement body type-checking logic as necessary
        return true; // Assuming body type checks pass
    }
    

    public static void main(String[] args) {
        // Global variables declaration: num V_somevar, text V_hellothe
        InternalTreeNode globVars = new InternalTreeNode("GLOBVARS");

        // Variable Declaration: num V_somevar
        InternalTreeNode varDecl1 = new InternalTreeNode("VarDecl");
        varDecl1.addChild(new LeafTreeNode(new Token(TokenClass.RESERVED_KEYWORD, "num"))); // Type: num
        varDecl1.addChild(new LeafTreeNode(new Token(TokenClass.VARIABLE, "V_somevar"))); // Variable: V_somevar

        // Variable Declaration: text V_hellothe
        InternalTreeNode varDecl2 = new InternalTreeNode("VarDecl");
        varDecl2.addChild(new LeafTreeNode(new Token(TokenClass.RESERVED_KEYWORD, "text"))); // Type: text
        varDecl2.addChild(new LeafTreeNode(new Token(TokenClass.VARIABLE, "V_hellothe"))); // Variable: V_hellothe

        // Adding variable declarations to global variables
        globVars.addChild(varDecl1);
        globVars.addChild(varDecl2);

        // Variable Declaration: num V_var1 (added for correctness)
        InternalTreeNode varDecl3 = new InternalTreeNode("VarDecl");
        varDecl3.addChild(new LeafTreeNode(new Token(TokenClass.RESERVED_KEYWORD, "num"))); // Type: num
        varDecl3.addChild(new LeafTreeNode(new Token(TokenClass.VARIABLE, "V_var1"))); // Variable: V_var1

        globVars.addChild(varDecl3); // Add V_var1 declaration to global variables

        // Algorithm section
        InternalTreeNode algo = new InternalTreeNode("ALGO");

        // V_var1 = 5;
        InternalTreeNode assignInstruc1 = new InternalTreeNode("ASSIGN");
        assignInstruc1.addChild(new LeafTreeNode(new Token(TokenClass.VARIABLE, "V_var1"))); // Variable: V_var1
        assignInstruc1.addChild(new LeafTreeNode(new Token(TokenClass.NUMBER, "5"))); // Constant: 5
        algo.addChild(assignInstruc1);

        // if grt(V_var1 , 10) then
        InternalTreeNode ifStmt = new InternalTreeNode("COMMAND");
        ifStmt.addChild(new LeafTreeNode(new Token(TokenClass.RESERVED_KEYWORD, "if"))); // if
        InternalTreeNode condition = new InternalTreeNode("COND");
        InternalTreeNode simpleCondition = new InternalTreeNode("SIMPLE");
        simpleCondition.addChild(new LeafTreeNode(new Token(TokenClass.NUMBER, "10"))); // 10
        simpleCondition.addChild(new LeafTreeNode(new Token(TokenClass.VARIABLE, "V_var1"))); // V_var1
        simpleCondition.addChild(new LeafTreeNode(new Token(TokenClass.BINOP, "grt"))); // grt
        condition.addChild(simpleCondition);
        ifStmt.addChild(condition);

        // then block
        InternalTreeNode thenBlock = new InternalTreeNode("begin");
        InternalTreeNode printStmt = new InternalTreeNode("print");
        printStmt.addChild(new LeafTreeNode(new Token(TokenClass.VARIABLE, "V_var1"))); // Variable: V_var1
        thenBlock.addChild(printStmt);
        ifStmt.addChild(thenBlock);

        // else block
        InternalTreeNode elseBlock = new InternalTreeNode("begin");
        InternalTreeNode assignInstruc2 = new InternalTreeNode("ASSIGN");
        assignInstruc2.addChild(new LeafTreeNode(new Token(TokenClass.VARIABLE, "V_var1"))); // Variable: V_var1
        InternalTreeNode addFunc = new InternalTreeNode("BINOP");
        addFunc.addChild(new LeafTreeNode(new Token(TokenClass.VARIABLE, "V_var1"))); // V_var1
        addFunc.addChild(new LeafTreeNode(new Token(TokenClass.NUMBER, "5"))); // 5
        assignInstruc2.addChild(addFunc);
        elseBlock.addChild(assignInstruc2);

        ifStmt.addChild(elseBlock);

        // Add if statement to the algorithm
        algo.addChild(ifStmt);

        // Functions section (empty for now)
        InternalTreeNode functions = new InternalTreeNode("FUNCTIONS");

        // Create the full program node
        InternalTreeNode program = new InternalTreeNode("PROG");
        program.addChild(globVars); // Global variables
        program.addChild(algo); // Algorithm
        program.addChild(functions); // Functions

        // TypeChecker instance
        TypeChecker typeChecker = new TypeChecker();

        // Run the type checker on the AST
        boolean result = typeChecker.checkProgram(program);

        // Output result
        if (result) {
            System.out.println("Type checking passed successfully!");
        } else {
            System.out.println("Type checking failed!");
        }
    }

}
