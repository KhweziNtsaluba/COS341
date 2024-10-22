import java.util.HashMap;
import java.util.Map;

public class TypeChecker {

    // Symbol table to store variable and function types
    private Map<String, String> symbolTable = new HashMap<>();

    // Entry point for type checking the entire AST
    public boolean checkProgram(ASTNode prog) {
        if (prog == null || prog.getChildren().size() < 3) return false;

        ASTNode globVars = prog.getChildren().get(0);
        ASTNode algo = prog.getChildren().get(1);
        ASTNode functions = prog.getChildren().get(2);

        return checkGlobalVars(globVars) && checkAlgo(algo) && checkFunctions(functions);
    }

    // Type check for global variables
    private boolean checkGlobalVars(ASTNode globVars) {
        if (globVars == null) return true; // Base case for nullable global vars

        for (ASTNode varDecl : globVars.getChildren()) {
            if (varDecl.getChildren().size() < 2) {
                System.out.println("Variable declaration is incomplete.");
                return false; // Should have at least type and name
            }

            String varType = typeof(varDecl.getChildren().get(0)); // Assuming 1st child is type
            String varName = varDecl.getChildren().get(1).getValue(); // Assuming 2nd child is variable name

            if (varType == null || varName == null) {
                System.out.println("Error: Variable type or name is null.");
                return false;
            }

            symbolTable.put(varName, varType); // Store in symbol table
        }
        return true;
    }

    // Type check for algorithm (ALGO ::= begin INSTRUC end)
private boolean checkAlgo(ASTNode algo) {
    if (algo == null || algo.getChildren().size() == 0) return false; // Algo must contain instructions

    ASTNode instruc = algo.getChildren().get(0); // ALGO has one child which is INSTRUC
    return checkInstruction(instruc); // Typecheck(ALGO) = typecheck(INSTRUC)
}

// Type check for individual instructions (INSTRUC)
private boolean checkInstruction(ASTNode instruc) {
    if (instruc == null || instruc.getValue() == null) return false;

    String commandType = instruc.getValue(); // Get the command type from instruction

    switch (commandType) {
        case "skip":
        case "halt":
            return true; // These commands are always valid

        case "print": {
            String type = typeof(instruc.getChildren().get(0)); // Atomic node being printed
            return type.equals("n") || type.equals("t"); // Only numeric or text types can be printed
        }

        case "return": {
            String returnType = typeof(instruc.getChildren().get(0)); // Atomic node being returned
            String funcType = "n"; // Placeholder for actual function return type (e.g., numeric)
            return returnType.equals(funcType);
        }

        case "assign": {
            String varName = instruc.getChildren().get(0).getValue(); // Variable name
            String termType = typeof(instruc.getChildren().get(1)); // Term node being assigned to variable
            String varType = symbolTable.get(varName); // Lookup variable type in symbol table

            return varType != null && varType.equals(termType); // Ensure types match
        }

        case "call": {
            String callType = typeof(instruc.getChildren().get(0)); // Call node type
            return callType.equals("v"); // Assume function call returns a valid type
        }

        case "branch": {
            String condType = typeof(instruc.getChildren().get(0)); // Condition type
            if (!condType.equals("b")) return false; // Condition must be boolean
            // Recursively typecheck both branches (INSTRUC1 and INSTRUC2)
            return checkInstruction(instruc.getChildren().get(1)) && checkInstruction(instruc.getChildren().get(2));
        }

        case "instruc": { 
            // INSTRUC1 ::= COMMAND ; INSTRUC2 => typecheck(COMMAND) ^ typecheck(INSTRUC2)
            ASTNode command = instruc.getChildren().get(0); // COMMAND
            ASTNode nextInstruc = instruc.getChildren().get(1); // INSTRUC2

            return checkInstruction(command) && checkInstruction(nextInstruc);
        }

        default:
            return false; // Unrecognized command
    }
}

    // Type check for functions (FUNCTIONS ::=)
private boolean checkFunctions(ASTNode functions) {
    if (functions == null || functions.getChildren().size() == 0) return true; // Base case: no functions

    // Typecheck(FUNCTIONS1) = typecheck(DECL) ^ typecheck(FUNCTIONS2)
    ASTNode functionDecl = functions.getChildren().get(0); // First function declaration (DECL)
    ASTNode remainingFunctions = functions.getChildren().size() > 1 ? functions.getChildren().get(1) : null; // Remaining FUNCTIONS2

    return checkFunction(functionDecl) && checkFunctions(remainingFunctions); // Recursively check DECL and FUNCTIONS2
}

// Type check for a single function declaration (DECL)
private boolean checkFunction(ASTNode functionDecl) {
    if (functionDecl == null || functionDecl.getChildren().size() < 2) return false; // Function must have header and body

    ASTNode header = functionDecl.getChildren().get(0); // Function header
    ASTNode body = functionDecl.getChildren().get(1); // Function body

    return checkHeader(header) && checkBody(body); // Type-check header and body
}


    // Type check for function headers
    private boolean checkHeader(ASTNode header) {
        String returnType = typeof(header.getChildren().get(0)); // First child is return type
        String funcName = header.getValue(); // Function name

        symbolTable.put(funcName, returnType); // Store function type in symbol table

        for (ASTNode param : header.getChildren().subList(1, header.getChildren().size())) {
            if (!typeof(param).equals("n")) { // Assuming all parameters must be numeric
                return false;
            }
        }
        return true;
    }

    // Type check for function bodies
    private boolean checkBody(ASTNode body) {
        if (body == null || body.getChildren().isEmpty()) return false;
        return checkAlgo(body.getChildren().get(0)); // Assuming body contains algorithm nodes
    }

    // Type checking for atomic values (variables, constants)
    private String typeof(ASTNode node) {
        if (node == null) return null; // Handle null case

        String nodeType = node.getValue(); // Get the value representing the node type

        if (nodeType == null) {
            System.out.println("Warning: ASTNode value is null");
            return null;
        }

        switch (nodeType) {
            case "Var":
                if (node.getChildren().isEmpty()) {
                    System.out.println("Error: Var node has no children.");
                    return null;
                }
                return symbolTable.get(node.getChildren().get(0).getValue()); // Variable name

            case "num":
                return "n"; // Numeric type
            case "text":
                return "t"; // Text type

            case "Const":
                return typeofConst(node); // Handle constants like numbers or text
            default:
                return "u"; // Undefined
        }
    }

    private String typeofConst(ASTNode constant) {
        String tokenClass = constant.getValue();
        if (tokenClass.equals("N"))
            return "n"; // Numeric constant
        if (tokenClass.equals("T"))
            return "t"; // Text constant
        return "u"; // Undefined
    }

    public static void main(String[] args) {
        // Create the root of the program AST: PROG ::= main GLOBVARS ALGO FUNCTIONS
        ASTNode program = new ASTNode("Program");

        // Create GLOBVARS ::= VTYP VNAME , GLOBVARS (or nullable)
        ASTNode globalVars = new ASTNode("GlobalVars");

        // Example global variable declaration: num myVar
        ASTNode varDecl = new ASTNode("VarDecl");
        ASTNode varType = new ASTNode("num"); // VTYP ::= num
        ASTNode varName = new ASTNode("V_myVar"); // VNAME ::= V_myVar
        varDecl.addChild(varType);
        varDecl.addChild(varName);
        globalVars.addChild(varDecl); // Add var declaration to globalVars

        // Create ALGO ::= begin INSTRUC end
        ASTNode algo = new ASTNode("Algo");
        ASTNode instruc = new ASTNode("skip");

        // Create COMMAND ::= print ATOMIC;
        ASTNode printCommand = new ASTNode("print");
        ASTNode atomic = new ASTNode("V_myVar"); // ATOMIC ::= VNAME
        printCommand.addChild(atomic);
        instruc.addChild(printCommand);
        algo.addChild(instruc); // Add INSTRUC to Algo

        // Create FUNCTIONS ::= DECL FUNCTIONS (nullable)
        ASTNode functions = new ASTNode("Functions");

        // Create function declaration: DECL ::= HEADER BODY
        ASTNode funcDecl = new ASTNode("FuncDecl");

        // HEADER ::= FTYP FNAME(VNAME, VNAME, VNAME)
        ASTNode funcHeader = new ASTNode("FuncHeader");
        ASTNode returnType = new ASTNode("num"); // FTYP ::= num
        ASTNode funcName = new ASTNode("F_myFunction"); // FNAME ::= F_myFunction
        ASTNode param1 = new ASTNode("V_param1"); // VNAME ::= V_param1
        ASTNode param2 = new ASTNode("V_param2"); // VNAME ::= V_param2
        ASTNode param3 = new ASTNode("V_param3"); // VNAME ::= V_param3
        funcHeader.addChild(returnType);
        funcHeader.addChild(funcName);
        funcHeader.addChild(param1);
        funcHeader.addChild(param2);
        funcHeader.addChild(param3);
        funcDecl.addChild(funcHeader);

        // BODY ::= PROLOG LOCVARS ALGO EPILOG SUBFUNCS end
        ASTNode funcBody = new ASTNode("Body");
        ASTNode prolog = new ASTNode("Prolog");
        funcBody.addChild(prolog);

        ASTNode localVars = new ASTNode("LocVars");

        ASTNode localVar1 = new ASTNode("VarDecl");
        ASTNode localVarType1 = new ASTNode("num");
        ASTNode localVarName1 = new ASTNode("V_local1");
        localVar1.addChild(localVarType1);
        localVar1.addChild(localVarName1);
        localVars.addChild(localVar1); 

        funcBody.addChild(localVars);

        ASTNode bodyAlgo = new ASTNode("Algo");
        ASTNode bodyInstruc = new ASTNode("Instruc");

        ASTNode returnInstruc = new ASTNode("return");
        ASTNode returnAtomic = new ASTNode("V_myVar");
        returnInstruc.addChild(returnAtomic);
        bodyInstruc.addChild(returnInstruc);

        bodyAlgo.addChild(bodyInstruc);
        funcBody.addChild(bodyAlgo);
        funcDecl.addChild(funcBody);

        functions.addChild(funcDecl); // Add function declaration to functions

        // Construct program
        program.addChild(globalVars);
        program.addChild(algo);
        program.addChild(functions);

        // Perform type checking
        TypeChecker checker = new TypeChecker();
        boolean typeCheckResult = checker.checkProgram(program);

        // Output result
        System.out.println("Type Check Result: " + typeCheckResult);
    }
}
