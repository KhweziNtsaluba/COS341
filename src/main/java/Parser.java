// import java.io.File;
// import java.util.Stack;


// public class Parser {
//     ParseTable parseTable;
//     private final Stack<Integer> stack = new Stack<>();
//     private String input;
//     private int index = 0;

//     Parser(ParseTable parseTable){
//         this.parseTable = parseTable;
//     }

//     Private Token readNextToken(){
//         // reads the next token
//     }


//     // stack := empty ; push(0,stack) ; read(next)
//     // loop
//     // case table[top(stack),next] of
//     //             shift s: push(s,stack) ;
//     //             read(next)

//     //     reduce p: n := the left-hand side of production p ;
//     //                     r := the number of symbols
//     //                     on the right-hand side of p ;
//     //                     pop r elements from the stack ;
//     //                     push(s,stack)
//     //                     where table[top(stack),n] = go s
//     // accept: terminate with success
//     // error: reportError
//     // endloop
//     void parse(File tokenFile){
//         // parse token file
//         // validate that input is indeed in the grammar
//         // resolve any conflicts/ambiguities by means of precedence
//         // create parse tree
//         // output [tree?] as xml 
//     }
// }
