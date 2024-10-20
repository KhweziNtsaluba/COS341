import java.util.List;

abstract class SyntaxNode {
    public abstract boolean typeCheck(SymbolTable symbolTable);
}

class ProgramNode extends SyntaxNode {
    private GlobalVarsNode globalVars;
    private AlgoNode algo;
    private FunctionsNode functions;

    public ProgramNode(GlobalVarsNode globalVars, AlgoNode algo, FunctionsNode functions) {
        this.globalVars = globalVars;
        this.algo = algo;
        this.functions = functions;
    }

    @Override
    public boolean typeCheck(SymbolTable symbolTable) {
        return globalVars.typeCheck(symbolTable) &&
               algo.typeCheck(symbolTable) &&
               functions.typeCheck(symbolTable);
    }
}

class GlobalVarsNode extends SyntaxNode {
    private List<VarDeclNode> variables;

    public GlobalVarsNode(List<VarDeclNode> variables) {
        this.variables = variables;
    }

    @Override
    public boolean typeCheck(SymbolTable symbolTable) {
        for (VarDeclNode var : variables) {
            if (!var.typeCheck(symbolTable)) {
                return false;
            }
        }
        return true;
    }
}

class VarDeclNode extends SyntaxNode {
    private String type;
    private String name;

    public VarDeclNode(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public boolean typeCheck(SymbolTable symbolTable) {
        symbolTable.add(name, type);  // Link variable to its type in the symbol table
        return true;
    }
}

class AlgoNode extends SyntaxNode {
    private List<InstructionNode> instructions;

    public AlgoNode(List<InstructionNode> instructions) {
        this.instructions = instructions;
    }

    @Override
    public boolean typeCheck(SymbolTable symbolTable) {
        for (InstructionNode instr : instructions) {
            if (!instr.typeCheck(symbolTable)) {
                return false;
            }
        }
        return true;
    }
}
