import java.util.HashMap;
import java.util.Map;

public class TypeChecker {

    // Symbol table to store variable and function types
    private Map<String, String> symbolTable = new HashMap<>();

    // Entry point for type checking the entire program
    public boolean typecheck(ProgramNode prog) {
        return typecheck(prog.getGlobVars()) && typecheck(prog.getAlgo()) && typecheck(prog.getFunctions());
    }

    // Type check for global variables
    private boolean typecheck(GlobalVarsNode globVars) {
        if (globVars == null) return true; // Base case
        VarDeclNode varDecl = globVars.getVarDecl();
        String varType = typeof(varDecl.getVarType());
        String varName = varDecl.getVarName();

        symbolTable.put(varName, varType); // Store in symbol table
  
        return typecheck(globVars.getNext());
    }

    // Type check for algorithm (instructions)
    private boolean typecheck(AlgoNode algo) {
        return algo == null || typecheck(algo.getInstruc());
    }

    // Type check for instructions
    private boolean typecheck(InstrucNode instruc) {
        if (instruc == null) return true; // Base case
        return typecheck(instruc.getCommand()) && typecheck(instruc.getNext());
    }

    // Type check for individual commands
    private boolean typecheck(CommandNode command) {
        if (command instanceof SkipCommand) return true;
        if (command instanceof HaltCommand) return true;

        if (command instanceof PrintCommand) {
            String type = typeof(((PrintCommand) command).getAtomic());
            return type.equals("n") || type.equals("t");
        }

        if (command instanceof ReturnCommand) {
            AtomicNode atomic = ((ReturnCommand) command).getAtomic();
            String atomicType = typeof(atomic);
            String funcType = findFunctionTypeInScope(command); // Assuming scope analysis is done

            return atomicType.equals(funcType);
        }

        if (command instanceof AssignCommand) {
            AssignCommand assign = (AssignCommand) command;
            String varType = symbolTable.get(assign.getVarName());
            String termType = typeof(assign.getTerm());

            return varType != null && varType.equals(termType);
        }

        if (command instanceof CallCommand) {
            String callType = typeof(((CallCommand) command).getCall());
            return callType.equals("v");
        }

        if (command instanceof BranchCommand) {
            BranchCommand branch = (BranchCommand) command;
            String condType = typeof(branch.getCond());
            if (!condType.equals("b")) return false;
            return typecheck(branch.getAlgo1()) && typecheck(branch.getAlgo2());
        }

        return false;
    }

    // Type checking for functions
    private boolean typecheck(FunctionsNode functions) {
        if (functions == null) return true; // Base case
        return typecheck(functions.getDecl()) && typecheck(functions.getNext());
    }

    private boolean typecheck(DeclNode decl) {
        return typecheck(decl.getHeader()) && typecheck(decl.getBody());
    }

    // Type check for function headers
    private boolean typecheck(HeaderNode header) {
        String returnType = typeof(header.getFtyp());
        String funcName = header.getFname();
        symbolTable.put(funcName, returnType); // Store function type in symbol table

        for (VarDeclNode param : header.getParams()) {
            if (!typeof(param.getVarType()).equals("n")) { // RecSPL only allows numeric arguments
                return false;
            }
        }
        return true;
    }

    // Type check for function bodies
    private boolean typecheck(BodyNode body) {
        return typecheck(body.getProlog()) && typecheck(body.getLocVars()) &&
               typecheck(body.getAlgo()) && typecheck(body.getEpilog()) &&
               typecheck(body.getSubFuncs());
    }

    // Type checking for atomic values (variables, constants)
    private String typeof(AtomicNode atomic) {
        if (atomic instanceof VarNode) {
            return symbolTable.get(((VarNode) atomic).getVarName());
        }
        if (atomic instanceof ConstNode) {
            return typeofConst((ConstNode) atomic);
        }
        return "u"; // Undefined
    }

    private String typeofConst(ConstNode constant) {
        if (constant.getTokenClass().equals("N")) return "n"; // Numeric constant
        if (constant.getTokenClass().equals("T")) return "t"; // Text constant
        return "u"; // Undefined
    }

    // Type check for terms (atomic, call, or operation)
    private String typeof(TermNode term) {
        if (term instanceof AtomicNode) {
            return typeof((AtomicNode) term);
        }
        if (term instanceof CallNode) {
            return typeof((CallNode) term);
        }
        if (term instanceof OpNode) {
            return typeof((OpNode) term);
        }
        return "u"; // Undefined
    }

    private String typeof(CallNode call) {
        for (AtomicNode arg : call.getArgs()) {
            if (!typeof(arg).equals("n")) return "u"; // All function arguments must be numeric
        }
        return symbolTable.get(call.getFname());
    }

    private String typeof(OpNode op) {
        if (op instanceof UnOpNode) {
            return typeofUnOp((UnOpNode) op);
        }
        if (op instanceof BinOpNode) {
            return typeofBinOp((BinOpNode) op);
        }
        return "u"; // Undefined
    }

    private String typeofUnOp(UnOpNode unOp) {
        if (unOp.getOpType().equals("not")) return "b"; // Boolean type
        if (unOp.getOpType().equals("sqrt")) return "n"; // Numeric type
        return "u"; // Undefined
    }

    private String typeofBinOp(BinOpNode binOp) {
        if (binOp.getOpType().equals("or") || binOp.getOpType().equals("and")) return "b";
        if (binOp.getOpType().equals("add") || binOp.getOpType().equals("sub") ||
            binOp.getOpType().equals("mul") || binOp.getOpType().equals("div")) return "n";
        if (binOp.getOpType().equals("eq") || binOp.getOpType().equals("grt")) return "c"; // Comparison type
        return "u"; // Undefined
    }

    private String findFunctionTypeInScope(CommandNode command) {
        // Placeholder for looking up the function's return type from the syntax tree scope
        return "n"; // Assume numeric type for simplicity
    }
}
