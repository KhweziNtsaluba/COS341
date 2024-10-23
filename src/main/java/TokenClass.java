public enum TokenClass {
    RESERVED_KEYWORD,         // Keywords defined in the language (e.g., 'main', 'begin', 'end', etc.)
    VARIABLE,                 // User-defined variable names
    USER_DEFINED_FUNCTION,     // User-defined functions
    TEXT,                     // String literals or text
    NUMBER,                   // Numeric literals
    END_OF_INPUT,            // Marks the end of input (EOF)
    BINOP,                   // Binary operators (e.g., +, -, *, /)
    UNOP,                    // Unary operators (e.g., not, sqrt)
    CONST,                   // Constant tokens, which can be numbers or text
    FNAME,                   // Function names
    VTYP,                    // Variable types (e.g., num, text)
    ATOMIC                   // Atomic elements in expressions (e.g., variables, constants)
}
