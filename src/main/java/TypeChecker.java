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
                return typeofConst(node.getChildren().get(0).getValue());

            case "true":
            case "false":
                return "b";

            case "Term":
                return typeof(node.getChildren().get(0));

            case "factor":
                return typeof(node.getChildren().get(0));

            default:
                System.out.println("Warning: Unrecognized type " + nodeType);
                return null;
        }
    }

    private String typeofConst(String value) {
        if (value.matches("[0-9]+"))
            return "n";
        else
            return "t";
    }

    public static void main(String[] args) {
        // Create an AST for a small program

        // Global variables section: int x
        InternalTreeNode globVars = new InternalTreeNode("GlobalVars");
        InternalTreeNode varDecl = new InternalTreeNode("VarDecl");
        varDecl.addChild(new LeafTreeNode(new Token(TokenClass.RESERVED_KEYWORD, "num"))); // Type: int
        varDecl.addChild(new LeafTreeNode(new Token(TokenClass.VARIABLE, "x"))); // Variable: x
        globVars.addChild(varDecl);

        // Algorithm section: x := 5;
        InternalTreeNode algo = new InternalTreeNode("Algo");
        InternalTreeNode assignInstruc = new InternalTreeNode("assign");
        assignInstruc.addChild(new LeafTreeNode(new Token(TokenClass.VARIABLE, "x"))); // Variable: x
        assignInstruc.addChild(new LeafTreeNode(new Token(TokenClass.NUMBER, "5"))); // Constant: 5
        algo.addChild(assignInstruc);

        // Functions section (empty for now)
        InternalTreeNode functions = new InternalTreeNode("Functions");

        // Create the full program node
        InternalTreeNode program = new InternalTreeNode("Program");
        program.addChild(globVars); // Global vars
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
