import java.util.List;

class FunctionNode extends SyntaxNode {
    private String functionName;
    private String returnType;
    private List<VarDeclNode> parameters;
    private AlgoNode functionBody;

    public FunctionNode(String functionName, String returnType, List<VarDeclNode> parameters, AlgoNode functionBody) {
        this.functionName = functionName;
        this.returnType = returnType;
        this.parameters = parameters;
        this.functionBody = functionBody;
    }

    @Override
    public boolean typeCheck(SymbolTable symbolTable) {
        // Add parameters to the symbol table
        for (VarDeclNode param : parameters) {
            if (!param.typeCheck(symbolTable)) {
                return false;
            }
        }

        // Check the function body
        return functionBody.typeCheck(symbolTable);
    }
}
