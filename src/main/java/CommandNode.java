class CommandNode extends SyntaxNode {
    @Override
    public boolean typeCheck(SymbolTable symbolTable) {
        // Logic for command nodes, e.g., checking the type of print, halt, skip
        return true;
    }
}

class AssignNode extends CommandNode {
    private String varName;
    private ExpressionNode expr;

    public AssignNode(String varName, ExpressionNode expr) {
        this.varName = varName;
        this.expr = expr;
    }

    @Override
    public boolean typeCheck(SymbolTable symbolTable) {
        String varType = symbolTable.getType(varName);
        String exprType = expr.getType(symbolTable);
        return varType != null && varType.equals(exprType);  // Ensure types match
    }
}

class ExpressionNode extends SyntaxNode {
    public String getType(SymbolTable symbolTable) {
        // To be overridden in subclasses
        return null;
    }
}

class AtomicNode extends ExpressionNode {
    private String value;  // Could be a variable or constant

    public AtomicNode(String value) {
        this.value = value;
    }

    @Override
    public String getType(SymbolTable symbolTable) {
        return symbolTable.getType(value);  // Fetch type from symbol table
    }
}
