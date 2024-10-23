public class SymbolInfo {
    String type; // variable type or function return type
    // Additional fields can include function parameter types, etc.

    public SymbolInfo(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "[" + this.type + "]";
    }
}