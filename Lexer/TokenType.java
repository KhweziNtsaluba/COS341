public enum TokenType {
    NUM,          // numeric types (Token-Class N)
    TEXT,         // text types (Token-Class T)
    VARIABLE,     // variable names (Token-Class V)
    FUNCTION,     // function names (Token-Class F)
    RESERVED_KEYWORD, // reserved keywords (like "main", "begin", etc.)
    OPERATOR,     // operators (e.g., +, -, *, /)
    DELIMITER,    // delimiters like commas, semicolons
    EOF           // end of file
}
