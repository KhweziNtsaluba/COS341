import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

class SymbolTable {
    // Stack of maps for block scoping
    private Stack<Map<String, String>> scopes;

    public SymbolTable() {
        // Initialize with a global scope
        scopes = new Stack<>();
        enterScope(); // Start with the global scope
    }

    // Add a new scope (e.g., when entering a function or block)
    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    // Exit the current scope
    public void exitScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    // Add a variable to the current scope
    public void add(String name, String type) {
        if (scopes.peek().containsKey(name)) {
            throw new RuntimeException("Error: Variable '" + name + "' is already declared in this scope.");
        }
        scopes.peek().put(name, type);
    }

    // Retrieve the type of a variable (searches all scopes, starting from the innermost)
    public String getType(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Map<String, String> scope = scopes.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        throw new RuntimeException("Error: Variable '" + name + "' not declared.");
    }

    // Check if a variable is declared (useful for type-checking)
    public boolean contains(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Map<String, String> scope = scopes.get(i);
            if (scope.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    // Print the current symbol table (for debugging)
    public void printCurrentScope() {
        if (!scopes.isEmpty()) {
            System.out.println("Current Scope: " + scopes.peek());
        }
    }
}
