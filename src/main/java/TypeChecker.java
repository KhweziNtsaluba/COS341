import java.util.HashMap;
import java.util.Map;

public class TypeChecker {

    // Symbol table to store variable and function types
    private Map<String, String> symbolTable = new HashMap<>();

    // Entry point for type checking the entire AST
    public boolean checkProgram(ASTNode prog) {
        // Assuming the first child is GlobalVars, second is Algo, and third is Functions
        if (prog.getChildren().size() < 3) return false;

        ASTNode globVars = prog.getChildren().get(0);
        ASTNode algo = prog.getChildren().get(1);
        ASTNode functions = prog.getChildren().get(2);

        return checkGlobalVars(globVars) && checkAlgo(algo) && checkFunctions(functions);
    }

    // Type check for global variables
    private boolean checkGlobalVars(ASTNode globVars) {
        if (globVars == null) return true; // Base case

        // Process each variable declaration
        for (ASTNode varDecl : globVars.getChildren()) {
            if (varDecl.getChildren().size() < 2) {
                System.out.println("Variable declaration does not have enough children.");
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

    // Type check for algorithm (instructions)
    private boolean checkAlgo(ASTNode algo) {
        // Assuming algo contains multiple instructions
        for (ASTNode instruc : algo.getChildren()) {
            if (!checkInstruction(instruc))
                return false;
        }
        return true;
    }

    // Type check for individual instructions
    private boolean checkInstruction(ASTNode instruc) {
        String commandType = instruc.getValue(); // Instruction type as value

        switch (commandType) {
            case "skip":
            case "halt":
                return true;

            case "print": {
                String type = typeof(instruc.getChildren().get(0)); // Atomic node being printed
                return type.equals("n") || type.equals("t");
            }

            case "return": {
                String returnType = typeof(instruc.getChildren().get(0)); // Atomic node being returned
                String funcType = "n"; // Placeholder for actual function return type
                return returnType.equals(funcType);
            }

            case "assign": {
                String varName = instruc.getChildren().get(0).getValue(); // Get the variable name
                String termType = typeof(instruc.getChildren().get(1)); // Term node being assigned
                String varType = symbolTable.get(varName);

                return varType != null && varType.equals(termType);
            }

            case "call": {
                String callType = typeof(instruc.getChildren().get(0)); // Call node
                return callType.equals("v");
            }

            case "branch": {
                String condType = typeof(instruc.getChildren().get(0)); // Condition node
                if (!condType.equals("b"))
                    return false;
                return checkAlgo(instruc.getChildren().get(1)) && checkAlgo(instruc.getChildren().get(2)); // Two branches
            }

            default:
                return false;
        }
    }

    // Type check for functions
    private boolean checkFunctions(ASTNode functions) {
        // Assuming functions node contains multiple function declarations
        for (ASTNode functionDecl : functions.getChildren()) {
            if (!checkFunction(functionDecl))
                return false;
        }
        return true;
    }

    // Type check for a single function declaration
    private boolean checkFunction(ASTNode functionDecl) {
        // Assuming first child is the header, second is the body
        ASTNode header = functionDecl.getChildren().get(0);
        ASTNode body = functionDecl.getChildren().get(1);

        return checkHeader(header) && checkBody(body);
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
        return checkAlgo(body.getChildren().get(0)); // Assuming body contains algorithm nodes
    }

    // Type checking for atomic values (variables, constants)
    private String typeof(ASTNode node) {
        if (node == null) return null; // Handle null case

        String nodeType = node.getValue(); // Get the value representing the node type

        if (nodeType == null) {
            System.out.println("Warning: ASTNode value is null");
            return null; // Added warning and return null if nodeType is null
        }

        switch (nodeType) {
            case "Var":
                if (node.getChildren().isEmpty()) {
                    System.out.println("Error: Var node has no children.");
                    return null; // Return null if no children are present
                }
                return symbolTable.get(node.getChildren().get(0).getValue()); // Variable name
            case "Const":
                return typeofConst(node);
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
        // Create the root of the program AST
        ASTNode program = new ASTNode("Program");

        // Create a GlobalVars node with a variable declaration
        ASTNode globalVars = new ASTNode("GlobalVars");
        ASTNode varDecl = new ASTNode("VarDecl"); // VarDecl -> type + name
        ASTNode varType = new ASTNode("n"); // 'n' stands for numeric
        ASTNode varName = new ASTNode("myVar");
        varDecl.addChild(varType); // Add type as first child
        varDecl.addChild(varName); // Add variable name as second child
        globalVars.addChild(varDecl); // Add var declaration to globalVars

        // Create an Algorithm node (Algo) with a print instruction
        ASTNode algo = new ASTNode("Algo");
        ASTNode printInstr = new ASTNode("print");
        ASTNode printValue = new ASTNode("Var");
        printValue.addChild(new ASTNode("myVar")); // Variable to be printed
        printInstr.addChild(printValue);
        algo.addChild(printInstr); // Add print instruction to algorithm

        // Create a Functions node with a function declaration
        ASTNode functions = new ASTNode("Functions");
        ASTNode funcDecl = new ASTNode("FuncDecl"); // FuncDecl -> header + body
        ASTNode funcHeader = new ASTNode("FuncHeader");
        ASTNode returnType = new ASTNode("n"); // Return type is numeric
        ASTNode funcNameNode = new ASTNode("myFunction");
        ASTNode param = new ASTNode("n"); // Parameter type is numeric
        funcHeader.addChild(returnType); // Add return type
        funcHeader.addChild(funcNameNode); // Add function name
        funcHeader.addChild(param); // Add parameter
        funcDecl.addChild(funcHeader); // Add header to function declaration

        ASTNode funcBody = new ASTNode("Algo"); // Function body as an algorithm
        ASTNode assignInstr = new ASTNode("assign");
        ASTNode assignVar = new ASTNode("Var");
        assignVar.addChild(new ASTNode("myVar")); // Assigning to myVar
        ASTNode assignValue = new ASTNode("Const");
        assignValue.addChild(new ASTNode("N")); // Assigning a numeric constant
        assignInstr.addChild(assignVar);
        assignInstr.addChild(assignValue);
        funcBody.addChild(assignInstr); // Add assign instruction to function body
        funcDecl.addChild(funcBody); // Add body to function declaration
        functions.addChild(funcDecl); // Add function declaration to functions

        // Build the program structure
        program.addChild(globalVars);
        program.addChild(algo);
        program.addChild(functions);

        // Create and run the type checker
        TypeChecker checker = new TypeChecker();
        boolean typeCheckPassed = checker.checkProgram(program);

        // Output result
        System.out.println("Type checking passed: " + typeCheckPassed);
    }
}
