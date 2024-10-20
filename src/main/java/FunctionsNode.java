import java.util.List;

class FunctionsNode extends SyntaxNode {
    private List<FunctionNode> functions;

    public FunctionsNode(List<FunctionNode> functions) {
        this.functions = functions;
    }

    @Override
    public boolean typeCheck(SymbolTable symbolTable) {
        for (FunctionNode function : functions) {
            if (!function.typeCheck(symbolTable)) {
                return false;
            }
        }
        return true;
    }
}
