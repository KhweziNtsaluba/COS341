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
                String type = typeof(instruc.getChildren().get(0));
                if (!type.equals("n") && !type.equals("t")) {
                    System.out.println("Error: Only numeric or text can be printed.");
                    return false;
                }
                return true;
            }

            case "assign": {
                String varName = instruc.getChildren().get(0).getValue();
                String termType = typeof(instruc.getChildren().get(1));
                String varType = symbolTable.get(varName);

                if (varType == null) {
                    System.out.println("Error: Variable " + varName + " not declared.");
                    return false;
                }

                if (!varType.equals(termType)) {
                    System.out.println("Error: Type mismatch in assignment to " + varName);
                    return false;
                }
                return true;
            }

            case "branch": {
                String condType = typeof(instruc.getChildren().get(0));
                if (!condType.equals("b")) {
                    System.out.println("Error: Condition must be boolean.");
                    return false;
                }

                return checkInstruction(instruc.getChildren().get(1)) && checkInstruction(instruc.getChildren().get(2));
            }

            default:
                System.out.println("Error: Unrecognized command " + commandType);
                return false;
        }
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
        if (node == null)
            return null;

        String nodeType = node.getValue();

        if (nodeType == null) {
            System.out.println("Warning: Node has no value.");
            return null;
        }

        switch (nodeType) {
            case "Var":
                return symbolTable.getOrDefault(node.getChildren().get(0).getValue(), null);

            case "num":
                return "n";

            case "text":
                return "t";

            case "Const":
                return typeofConst(node);

            default:
                return "u"; // Undefined
        }
    }

    private String typeofConst(ASTNode constant) {
        String tokenClass = constant.getValue();
        if (tokenClass.equals("N"))
            return "n"; 
        if (tokenClass.equals("T"))
            return "t"; 
        return "u"; 
    }

    public static void main(String[] args) {
        // Create the root of the program AST: PROG ::= main GLOBVARS ALGO FUNCTIONS
        ASTNode program = new ASTNode("Program");

        // Create global variables: num myVar
        ASTNode globalVars = new ASTNode("GlobalVars");
        ASTNode varDecl = new ASTNode("VarDecl");
        ASTNode varType = new ASTNode("num");
        ASTNode varName = new ASTNode("V_myVar");
        varDecl.addChild(varType);
        varDecl.addChild(varName);
        globalVars.addChild(varDecl);

        // Create an algorithm (ALGO) node: begin skip end
        ASTNode algo = new ASTNode("Algo");
        ASTNode instruc = new ASTNode("Instruc");
        ASTNode skipCommand = new ASTNode("skip");
        instruc.addChild(skipCommand);
        algo.addChild(instruc);

        // Create a function declaration: num F_myFunction(num V_param1, num V_param2)
        ASTNode functions = new ASTNode("Functions");
        ASTNode funcDecl = new ASTNode("FuncDecl");

        // Function header: num F_myFunction
        ASTNode funcHeader = new ASTNode("FuncHeader");
        ASTNode returnType = new ASTNode("num");
        ASTNode funcName = new ASTNode("F_myFunction");
        funcHeader.addChild(returnType);
        funcHeader.addChild(funcName);

        // Function parameters: (num V_param1, num V_param2)
        ASTNode paramDecl1 = new ASTNode("parDec");
        ASTNode paramType1 = new ASTNode("num");
        ASTNode paramName1 = new ASTNode("V_param1");
        paramDecl1.addChild(paramType1);
        paramDecl1.addChild(paramName1);

        ASTNode paramDecl2 = new ASTNode("parDec");
        ASTNode paramType2 = new ASTNode("num");
        ASTNode paramName2 = new ASTNode("V_param2");
        paramDecl2.addChild(paramType2);
        paramDecl2.addChild(paramName2);

        // Add parameters to the function header
        funcHeader.addChild(paramDecl1);
        funcHeader.addChild(paramDecl2);

        // Function body: return V_myVar;
        ASTNode funcBody = new ASTNode("Body");
        ASTNode returnInstr = new ASTNode("return");
        ASTNode returnVar = new ASTNode("V_myVar");
        returnInstr.addChild(returnVar);
        funcBody.addChild(returnInstr);

        // Add the header and body to the function declaration
        funcDecl.addChild(funcHeader);
        funcDecl.addChild(funcBody);

        // Add the function declaration to the functions section
        functions.addChild(funcDecl);

        // Add global variables, algorithm, and functions to the program
        program.addChild(globalVars);
        program.addChild(algo);
        program.addChild(functions);

        // Perform type checking on the constructed program
        TypeChecker checker = new TypeChecker();
        boolean result = checker.checkProgram(program);

        // Output the result
        System.out.println("Type check result: " + (result ? "Success" : "Failure"));
    }
}
