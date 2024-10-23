import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable {
    private Stack<HashMap<String, SymbolInfo>> scopes = new Stack<>();

    public SymbolTable() {
        // Push a new global scope on initialization
        pushNewScope();
    }

    public void pushNewScope() {
        scopes.push(new HashMap<>());

        // for debugging
        printSymbolTable();
    }

    /////// persist variable names to global hash table?
    public void popScope() {

        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    // declare a variable
    public void bind(String name, SymbolInfo info) throws Exception {
        HashMap<String, SymbolInfo> currentScope = scopes.peek();

        /* No variable may be declared twice in the same scope */
        if (currentScope.containsKey(name)) {
            throw new Exception("Variable " + name + " is already declared in this scope.");
        }
        currentScope.put(name, info);

        // for debugging
        System.out.println("BINGING" + name + " to symbol table");
        printSymbolTable();
    }


    SymbolInfo lookup(String name, boolean globalSearch){
        // starting from the nearest/innermost scope, find the nearest variable
        /* If a used variable is declared more than once, in different scopes, nearest one takes precedence */

        // Check the current scope first
        int numScopes = scopes.size();
        SymbolInfo symbol = scopes.get(numScopes - 1).get(name);

        if (symbol != null || !globalSearch) {
            return symbol;
        }

        for (int i = numScopes - 2; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return scopes.get(i).get(name);
            }
        }
        return null; // variable not found
    }

    public void printSymbolTable() {
        System.out.println("=== Symbol Table ===");
    
        // Iterate over the scopes stack
        for (int scopeIndex = 0; scopeIndex < scopes.size(); scopeIndex++) {
            System.out.println("Scope Level " + scopeIndex + ":");
            
            // Get the scope (HashMap<String, SymbolInfo>)
            HashMap<String, SymbolInfo> currentScope = scopes.get(scopeIndex);
            
            // Print each entry in the scope
            for (Map.Entry<String, SymbolInfo> entry : currentScope.entrySet()) {
                String name = entry.getKey();
                SymbolInfo info = entry.getValue();
                System.out.println("    " + name + " -> " + info);
            }
            
            System.out.println("------------------------");
        }
    
        System.out.println("=== End of Symbol Table ===");
    }
    
}
