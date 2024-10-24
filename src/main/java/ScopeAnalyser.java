import java.util.*;
public class ScopeAnalyser {

    private SymbolTable symbolTable; //to be returned
    private int funcCounter = 0;
    private int varCounter = 0;

    private HashSet<String> reservedKeywordsSet;

    public ScopeAnalyser() {
        this.symbolTable = new SymbolTable();
        // this.reservedKeywordsSet = new HashSet<>();
        // for (String keyword : Lexer.KEYWORDS) {
        //     reservedKeywordsSet.add(keyword);
        // }
    }

    // Generates a unique internal function name
    private String generateUniqueInternalFuncName() {
        return "_func" + (funcCounter++);
    }

    // Generates a unique internal variable name
    private String generateUniqueInternalVarName() {
        return "_var" + (varCounter++);
    }
    
    // crawl through tree handling scopes -- returns symbol table
    public SymbolTable crawl(ASTNode rootNode) throws Exception {
        traverse(rootNode);
        return symbolTable;
    }

    // Recursively visit AST nodes and apply semantic checks
    private void traverse(ASTNode node) throws Exception {
        if (node instanceof InternalTreeNode) {
            InternalTreeNode internalNode = (InternalTreeNode) node;

            // Handle specific node types based on grammar
            switch (internalNode.getGrammarVariable()) {

                case "PROG":
                for (ASTNode child : internalNode.getChildren()) {
                    traverse(child);
                }
                break;

                //////////// global variables ////////////
                // Handle global variable declarations
                case "GLOBVARS":
                    handleGlobalVarDeclarations(internalNode);
                    break;

                
                //////////// Function Declarations ////////////
                // Process all function declarations (if there are any)
                case "FUNCTIONS":
                    if (!internalNode.getChildren().isEmpty()) {
                        InternalTreeNode declNode = (InternalTreeNode) internalNode.getChildren().get(0); // DECL
                        handleFunctionDeclaration(declNode);
                        InternalTreeNode functionsNode = (InternalTreeNode) internalNode.getChildren().get(1); // FUNCTIONS
                        crawl(functionsNode); // Recursively handle next function
                    }
                    break;
                
                //////////// local variables ////////////
                // Handle algorithm: instructions within the "begin ... end" block
                // ALGO -> begin INSTRUC end
                case "ALGO": {
                    InternalTreeNode instrucNode = (InternalTreeNode) internalNode.getChildren().get(1); // INSTRUC
                    crawl(instrucNode); // Process the instructions
                    break;
                }
                
                // Process each instruction within the algorithm (if there are any)
                // INSTRUC -> COMMAND ; INSTRUC 
                case "INSTRUC":
                    if (!internalNode.getChildren().isEmpty()) {
                        InternalTreeNode commandNode = (InternalTreeNode) internalNode.getChildren().get(0); // COMMAND
                        handleCommand(commandNode); // Process command
                        InternalTreeNode instrucNode = (InternalTreeNode) internalNode.getChildren().get(2); // Next INSTRUC
                        crawl(instrucNode); // Process remaining instructions
                    }
                break;

                case "ATOMIC": {
                    handleAtomic(internalNode);
                    break;
                }

                case "TERM": {
                    handleTerm(internalNode);
                    break;
                }

                default:
                    throw new Exception(internalNode.getGrammarVariable() + " has no traverse rules");
            }

        }
        else if (node instanceof LeafTreeNode){ // extract token
            LeafTreeNode leafNode = (LeafTreeNode) node;
            Token token = leafNode.getToken();
        }
    }

    ///////////////////////////// Helpers ////////////////////////////////

    //////////// global variable helpers ////////////////

    private void handleGlobalVarDeclarations(InternalTreeNode globVarsNode) throws Exception {
        // Base case: empty GLOBVARS (i.e. epsilon)
        if (globVarsNode.getChildren().isEmpty()) {
            return;
        }
        
        // Recursive case: VTYP VNAME , GLOBVARS
        // Expecting children in the order: [VTYP, VNAME, optional ',', GLOBVARS]
        
        // 1. Extract VTYP (variable type)
        InternalTreeNode vtypNode = (InternalTreeNode) globVarsNode.getChildren().get(0); // VTYP
        ASTNode vtypChild = (LeafTreeNode) vtypNode.getChildren().get(0);
        String varType = vtypChild.toString(); // 'num' or 'text'
        
        // 2. Extract VNAME (variable name)
        InternalTreeNode vnameNode = (InternalTreeNode) globVarsNode.getChildren().get(1);
        String varName = extractVariableName(vnameNode);

        // 3. Declare the variable in the symbol table (global scope)
        symbolTable.bind(varName, new SymbolInfo(varType));  

        // 4. If there is a following GLOBVARS (i.e., recursive call for more global variables)
        // GLOBVARS -> VTYP VNAME , GLOBVARS 
        if (globVarsNode.getChildren().size() > 3) {
            InternalTreeNode nextGlobVarsNode = (InternalTreeNode) globVarsNode.getChildren().get(3); // GLOBVARS
            handleGlobalVarDeclarations(nextGlobVarsNode);
        }

    }
    
    private String extractVariableName(InternalTreeNode vnameNode) {
        LeafTreeNode variableNode = (LeafTreeNode) vnameNode.getChildren().get(0);
        return variableNode.getToken().getTokenWord();
    }
    
    ////////////// Function Declaration Helpers ////////////////
    
    private void handleFunctionDeclaration(InternalTreeNode declNode) throws Exception {
        InternalTreeNode headerNode = (InternalTreeNode) declNode.getChildren().get(0); // HEADER
        handleFunctionHeader(headerNode); // Process function signature

        InternalTreeNode bodyNode = (InternalTreeNode) declNode.getChildren().get(1); // BODY
        
        // Enter a new scope for the function body
        /* Every function declaration opens its own scope */
        symbolTable.pushNewScope();
        crawl(bodyNode); // Process the function body (BODY -> PROLOG LOCVARS ALGO EPILOG SUBFUNCS end)
        symbolTable.popScope();
    }

    private void handleFunctionHeader(InternalTreeNode headerNode) throws Exception {
        // 1. Extract function type (FTYP)
        InternalTreeNode ftypNode = (InternalTreeNode) headerNode.getChildren().get(0); // FTYP
        ASTNode ftypChild = (LeafTreeNode) ftypNode.getChildren().get(0); 
        String functionType = ftypChild.toString(); // 'num' or 'void'

        // 2. Extract function name (FNAME)
        InternalTreeNode fnameNode = (InternalTreeNode) headerNode.getChildren().get(1); // FNAME
        String functionName = extractFunctionName(fnameNode);

        // Check if function name already exists in the IMMEDIATE parent scope
        if (symbolTable.lookup(functionName, false) != null) {
            throw new Exception("Function '" + functionName + "' is already declared.");
        }

        // Declare the function in the symbol table
        symbolTable.bind(functionName, new SymbolInfo(functionType));

        // 3. Declare the parameters (VNAMEs) in the function's scope
        // HEADER -> FTYP FNAME ( VNAME , VNAME , VNAME )
        for (int i = 0; i < 3; i++) {
            InternalTreeNode vnameNode = (InternalTreeNode) headerNode.getChildren().get(i * 2 + 3); // VNAME
            String paramName = extractVariableName(vnameNode);

            // assuming all arguments will be of type num
            symbolTable.bind(paramName, new SymbolInfo("num"));
        }
    }

    // FNAME -> a token of Token-Class F from the Lexer
    private String extractFunctionName(InternalTreeNode fnameNode) {
        LeafTreeNode functionNode = (LeafTreeNode) fnameNode.getChildren().get(0); // Get the function name token
        return functionNode.getToken().getTokenWord();
    }


    ///////////// Command Helpers ////////////////

    // COMMAND -> skip | halt | print ATOMIC | ASSIGN | CALL | BRANCH
    private void handleCommand(InternalTreeNode commandNode) throws Exception {
        ASTNode commandChild = commandNode.getChildren().get(0);
        String commandType = null;

        if(commandChild instanceof InternalTreeNode){
            commandType = ((InternalTreeNode) commandChild).getGrammarVariable();
        }
        else if (commandChild instanceof LeafTreeNode){
            commandType = ((LeafTreeNode) commandChild).toString();
        }
        else {
            throw new Exception("Node has invalid instance type");
        }

        // handle different types of commands
        switch (commandType) {
            // No semantic action needed for "skip" or "halt"
            case "skip":
            case "halt":
            break;

            case "print":
                InternalTreeNode atomicNode = (InternalTreeNode) commandNode.getChildren().get(1); // ATOMIC
                crawl(atomicNode); // Check if the variable exists -- handle local variable usage
                break;
            
            case "ASSIGN":
                handleAssignment(commandNode);
                break;

            case "CALL":
                handleFunctionCall(commandNode);
                break;
    
            // case "BRANCH":
            //     handleBranch(commandNode);
            //     break;
            
            default:
                throw new Exception("Unknown COMMAND: " + commandType);
    
        }
    }

    // ASSIGN -> VNAME <input | VNAME = TERM
    private void handleAssignment(InternalTreeNode assignNode) throws Exception {
        InternalTreeNode vnameNode = (InternalTreeNode) assignNode.getChildren().get(0); // VNAME
        String varName = extractVariableName(vnameNode);

        // Ensure the variable has been declared
        if (symbolTable.lookup(varName, true) == null) {
            throw new Exception("Undeclared variable: " + varName);
        }

        // Process the right-hand side (TERM)
        if (assignNode.getChildren().size() > 2) {
            InternalTreeNode termNode = (InternalTreeNode) assignNode.getChildren().get(2); // TERM
            crawl(termNode); // Process the term (expression or function call)
        }
    }

    // TERM -> ATOMIC | CALL | OP
    private void handleTerm(InternalTreeNode termNode) throws Exception {
         // Check if the term is ATOMIC, CALL, or OP
        InternalTreeNode termChild = (InternalTreeNode) termNode.getChildren().get(0);
        String termType = termChild.getGrammarVariable();

        switch (termType) {
            case "ATOMIC":
                handleAtomic(termChild); // Check variable existence or process constant
                break;
    
            case "CALL":
                handleFunctionCall(termChild); // Ensure function exists and arguments are valid
                break;
    
            case "OP":
                handleOperation(termChild); // Process operation recursively
                break;
    
            default:
                throw new Exception("Unknown TERM type: " + termType);
        }
    }

    // ATOMIC must always be declared (if it does not evaluate to CONST)
    private void handleAtomic(InternalTreeNode atomicNode) throws Exception {
        InternalTreeNode atomicChild = (InternalTreeNode) atomicNode.getChildren().get(0); // VNAME or CONST
        String atomicChildType = atomicChild.getGrammarVariable();

        if(atomicChildType == "VNAME"){
            // handle vname case - check if variable is defined
            String varName = extractVariableName(atomicChild);
            SymbolInfo varInfo = symbolTable.lookup(varName, true);

            if (varInfo == null) {
                throw new Exception("Variable '" + varName + "' is used but not declared.");
            }
        }
        else if (atomicChildType == "CONST"){
            // handle const case - Constant: No need for declaration check
            LeafTreeNode constLeafNode = (LeafTreeNode) atomicChild.getChildren().get(0); // a token of Token-Class N from the Lexer
            String constantValue = atomicNode.toString();
        }
    }


    ////////////// Function Call Helpers ///////////////

    // CALL -> FNAME( ATOMIC , ATOMIC , ATOMIC )
    private void handleFunctionCall(InternalTreeNode callNode) throws Exception {
        // Extract the function name (FNAME)
        InternalTreeNode fnameNode = (InternalTreeNode) callNode.getChildren().get(0); // FNAME
        String functionName = extractFunctionName(fnameNode);

        SymbolInfo functionInfo = symbolTable.lookup(functionName, true);

        if (functionInfo == null) { // function not declared
            throw new Exception("Function '" + functionName + "' is called but not declared.");
        }

        // Process function arguments (if any)
        for (int i = 0; i < 3; i++) {
            InternalTreeNode argNode = (InternalTreeNode) callNode.getChildren().get(i * 2 + 2); // ATOMIC
            handleAtomic(argNode); // Ensure the arguments are valid (could be variables or constants)
        }
    }

    ////////////// Operation Call Helpers ///////////////

    //OP -> UNOP( ARG ) | BINOP( ARG , ARG )
    public void handleOperation(InternalTreeNode opNode) throws Exception {
        InternalTreeNode opChild = (InternalTreeNode) opNode.getChildren().get(0); // UNOP or BINOP
        String opChildType = opChild.getGrammarVariable();

        if (opChildType.equals("UNOP")) {
            // Unary operation: process the argument
            InternalTreeNode argNode = (InternalTreeNode) opNode.getChildren().get(2); // ARG
            handleArgument(argNode); // Process the term inside the unary operation
        }
        else if (opChildType.equals("BINOP")) {
            // Binary operation: process both arguments
            InternalTreeNode arg1Node = (InternalTreeNode) opNode.getChildren().get(2); // First ARG
            InternalTreeNode arg2Node = (InternalTreeNode) opNode.getChildren().get(4); // Second ARG
            handleArgument(arg1Node); // Process the first term
            handleArgument(arg2Node); // Process the second term
        }
        else {
            throw new Exception("Unknown operation type: " + opChildType);
        }
    }

    // ARG -> ATOMIC | OP
    public void handleArgument(InternalTreeNode argNode) throws Exception {
        InternalTreeNode argChildNode = (InternalTreeNode) argNode.getChildren().get(0);
        String argChildType = argNode.getGrammarVariable();

        switch (argChildType) {
            case "ATOMIC":
                handleAtomic(argChildNode);
            case "OP":
                handleOperation(argChildNode);
            default:
            throw new UnsupportedOperationException("Unknown ARG type: " + argChildType);
        }
    }

    ////////////// Function Body Helpers ///////////////

    // Handle InternalTreeNode (non-terminal nodes)
    private void handleInternalNode(InternalTreeNode node) {
        String grammarVariable = node.getGrammarVariable();

        
    }

    // variable restrictions - an exception MUST be thrown if violated:
        /*
         * Every used variable name must have a declaration
      >   * No variable may be declared twice in the same scope
      >   * If a used variable is declared more than once, in different scopes, nearest one takes precedence
         * Declaration of a used variable must be in the current scope or ancestor scope
         * No variable name can have the same name as a function name (anywhere in the program)
         * No variable name can be identical to a reserved keyword
         * Two variables are different computational entities if they are rooted in different scopes
         */

    // function restrictions - ditto
        /*
         * the `main` program forms the highest level scope with no parent
        > * Every function declaration opens its own scope
        > * Child scope may not have the same name as its IMMEDIATE parent scope
         * Child scope cannot have the same name of any sibling scope (under the same parent)
         * A call command may refer to an IMMEDIATE child scope
         * A call command may refer to its own scope (RECURSION)
         * There may be no recursive calls to main
         */
   // private AST parseXML(){} // parses xml to produce syntax tree

}

// produces a symbol table

