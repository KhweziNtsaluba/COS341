import java.util.Hashtable;

public class ScopeAnalyser {

    private SymbolTable symbolTable; //to be returned

    private String generateUniqueInternalFuncName(){}
    private String generateUniqueInternalVarName(){}
    private SymbolTable crawl(AST){} // crawls through tree, extracts semantic
                                     // information and returns symbol table

    // variable restrictions - an exception MUST be thrown if violated:
        /*
         * Every used variable name must have a declaration
         * No variable may be declared twice in the same scope
         * If a used variable is declared more than once, in different scopes, nearest one takes precedence
         * Declaration of a used variable must be in the current scope or ancestor scope
         * No variable name can have the same name as a function name (anywhere in the program)
         * No variable name can be identical to a reserved keyword
         * Two variables are different computational entities if they are rooted in different scopes
         */

    // function restrictions - ditto
        /*
         * the `main` program forms the highest level scope with no parent
         * Every function declaration opens its own scope
         * Child scope may not have the same name as its IMMEDIATE parent scope
         * Child scope cannot have the same name of any sibling scope (under the same parent)
         * A call command may refer to an IMMEDIATE child scope
         * A call command may refer to its own scope (RECURSION)
         * There may be no recursive calls to main
         */
    private AST parseXML(){} // parses xml to produce syntax tree

}

// produces a symbol table

