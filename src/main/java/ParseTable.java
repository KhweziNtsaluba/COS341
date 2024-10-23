import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ParseTable {

    // Action types
    public enum ActionType {
        SHIFT, REDUCE, ACCEPT, ERROR
    }

    public static class Action {
        private ActionType type;
        private int number;  // For shift/reduce actions, this will be the state/production number

        public Action(ActionType type, int number) {
            this.type = type;
            this.number = number;
        }

        public Action(ActionType type) {
            this.type = type;
        }

        public ActionType getType() {
            return type;
        }

        public int getNumber() {
            return number;
        }

        @Override
        public String toString() {
            if (type == ActionType.ACCEPT) {
                return "acc";
            } else if (type == ActionType.SHIFT) {
                return "s" + number;
            } else if (type == ActionType.REDUCE) {
                return "r" + number;
            } else {
                return "error";
            }
        }
    }

    private Map<Integer, Map<String, Action>> actionTable;  // actions to take given a state cross-indexed with a terminal symbol
    private Map<Integer, Map<String, Integer>> gotoTable;   // actions to take given a state cross-indexed with a non-terminal symbol 

    ParseTable(){
        actionTable =  new HashMap<>();
        gotoTable = new HashMap<>();

        addAction(0, "main", new Action(ActionType.SHIFT, 1));
        addAction(1, "num", new Action(ActionType.SHIFT, 4));
        addAction(1, "text", new Action(ActionType.SHIFT, 5));
        addAction(1, "begin", new Action(ActionType.REDUCE, 1));
        addGoto(1, "GLOBVARS", 2);
        addGoto(1, "VTYP", 3);
        addAction(2, "begin", new Action(ActionType.SHIFT, 7));
        addGoto(2, "ALGO", 6);
        addAction(3, "V", new Action(ActionType.SHIFT, 9));
        addGoto(3, "VNAME", 8);
        addAction(4, "V", new Action(ActionType.REDUCE, 3));
        addAction(5, "V", new Action(ActionType.REDUCE, 4));
        addAction(6, "num", new Action(ActionType.SHIFT, 14));
        addAction(6, "end", new Action(ActionType.REDUCE, 46));
        addAction(6, "void", new Action(ActionType.SHIFT, 15));
        addAction(6, "$", new Action(ActionType.REDUCE, 46));
        addGoto(6, "FUNCTIONS", 10);
        addGoto(6, "DECL", 11);
        addGoto(6, "HEADER", 12);
        addGoto(6, "FTYP", 13);
        addAction(7, "V", new Action(ActionType.SHIFT, 9));
        addAction(7, "end", new Action(ActionType.REDUCE, 7));
        addAction(7, "skip", new Action(ActionType.SHIFT, 18));
        addAction(7, "halt", new Action(ActionType.SHIFT, 19));
        addAction(7, "print", new Action(ActionType.SHIFT, 20));
        addAction(7, "if", new Action(ActionType.SHIFT, 27));
        addAction(7, "F", new Action(ActionType.SHIFT, 28));
        addAction(7, "return", new Action(ActionType.SHIFT, 24));
        addGoto(7, "VNAME", 25);
        addGoto(7, "INSTRUC", 16);
        addGoto(7, "COMMAND", 17);
        addGoto(7, "ASSIGN", 21);
        addGoto(7, "CALL", 22);
        addGoto(7, "BRANCH", 23);
        addGoto(7, "FNAME", 26);
        addAction(8, ",", new Action(ActionType.SHIFT, 29));
        addAction(9, ",", new Action(ActionType.REDUCE, 5));
        addAction(9, ";", new Action(ActionType.REDUCE, 5));
        addAction(9, "<input", new Action(ActionType.REDUCE, 5));
        addAction(9, "=", new Action(ActionType.REDUCE, 5));
        addAction(9, ")", new Action(ActionType.REDUCE, 5));
        addAction(11, "num", new Action(ActionType.SHIFT, 14));
        addAction(11, "end", new Action(ActionType.REDUCE, 46));
        addAction(11, "void", new Action(ActionType.SHIFT, 15));
        addAction(11, "$", new Action(ActionType.REDUCE, 46));
        addGoto(11, "FUNCTIONS", 30);
        addGoto(11, "DECL", 11);
        addGoto(11, "HEADER", 12);
        addGoto(11, "FTYP", 13);
        addAction(12, "{", new Action(ActionType.SHIFT, 33));
        addGoto(12, "BODY", 31);
        addGoto(12, "PROLOG", 32);
        addAction(13, "F", new Action(ActionType.SHIFT, 28));
        addGoto(13, "FNAME", 34);
        addAction(14, "F", new Action(ActionType.REDUCE, 50));
        addAction(15, "F", new Action(ActionType.REDUCE, 51));
        addAction(16, "end", new Action(ActionType.SHIFT, 35));
        addAction(17, ";", new Action(ActionType.SHIFT, 36));
        addAction(18, ";", new Action(ActionType.REDUCE, 9));
        addAction(19, ";", new Action(ActionType.REDUCE, 10));
        addAction(20, "V", new Action(ActionType.SHIFT, 9));
        addAction(20, "N", new Action(ActionType.SHIFT, 40));
        addAction(20, "T", new Action(ActionType.SHIFT, 41));
        addGoto(20, "VNAME", 38);
        addGoto(20, "ATOMIC", 37);
        addGoto(20, "CONST", 39);
        addAction(21, ";", new Action(ActionType.REDUCE, 12));
        addAction(22, ";", new Action(ActionType.REDUCE, 13));
        addAction(23, ";", new Action(ActionType.REDUCE, 14));
        addAction(24, "V", new Action(ActionType.SHIFT, 9));
        addAction(24, "N", new Action(ActionType.SHIFT, 40));
        addAction(24, "T", new Action(ActionType.SHIFT, 41));
        addGoto(24, "VNAME", 38);
        addGoto(24, "ATOMIC", 42);
        addGoto(24, "CONST", 39);
        addAction(25, "<input", new Action(ActionType.SHIFT, 43));
        addAction(25, "=", new Action(ActionType.SHIFT, 44));
        addAction(26, "(", new Action(ActionType.SHIFT, 45));
        addAction(27, "not", new Action(ActionType.SHIFT, 59));
        addAction(27, "sqrt", new Action(ActionType.SHIFT, 60));
        addAction(27, "or", new Action(ActionType.SHIFT, 51));
        addAction(27, "and", new Action(ActionType.SHIFT, 52));
        addAction(27, "eq", new Action(ActionType.SHIFT, 53));
        addAction(27, "grt", new Action(ActionType.SHIFT, 54));
        addAction(27, "add", new Action(ActionType.SHIFT, 55));
        addAction(27, "sub", new Action(ActionType.SHIFT, 56));
        addAction(27, "mul", new Action(ActionType.SHIFT, 57));
        addAction(27, "div", new Action(ActionType.SHIFT, 58));
        addGoto(27, "COND", 46);
        addGoto(27, "SIMPLE", 47);
        addGoto(27, "COMPOSIT", 48);
        addGoto(27, "UNOP", 50);
        addGoto(27, "BINOP", 49);
        addAction(28, "(", new Action(ActionType.REDUCE, 45));
        addAction(29, "num", new Action(ActionType.SHIFT, 4));
        addAction(29, "text", new Action(ActionType.SHIFT, 5));
        addAction(29, "begin", new Action(ActionType.REDUCE, 1));
        addGoto(29, "GLOBVARS", 61);
        addGoto(29, "VTYP", 3);
        addAction(30, "end", new Action(ActionType.REDUCE, 47));
        addAction(30, "$", new Action(ActionType.REDUCE, 47));
        addAction(31, "num", new Action(ActionType.REDUCE, 48));
        addAction(31, "end", new Action(ActionType.REDUCE, 48));
        addAction(31, "void", new Action(ActionType.REDUCE, 48));
        addAction(31, "$", new Action(ActionType.REDUCE, 48));
        addAction(32, "num", new Action(ActionType.SHIFT, 4));
        addAction(32, "text", new Action(ActionType.SHIFT, 5));
        addGoto(32, "VTYP", 63);
        addGoto(32, "LOCVARS", 62);
        addAction(33, "num", new Action(ActionType.REDUCE, 53));
        addAction(33, "text", new Action(ActionType.REDUCE, 53));
        addAction(34, "(", new Action(ActionType.SHIFT, 64));
        addAction(35, "num", new Action(ActionType.REDUCE, 6));
        addAction(35, ";", new Action(ActionType.REDUCE, 6));
        addAction(35, "else", new Action(ActionType.REDUCE, 6));
        addAction(35, "void", new Action(ActionType.REDUCE, 6));
        addAction(35, "}", new Action(ActionType.REDUCE, 6));
        addAction(35, "$", new Action(ActionType.REDUCE, 6));
        addAction(36, "V", new Action(ActionType.SHIFT, 9));
        addAction(36, "end", new Action(ActionType.REDUCE, 7));
        addAction(36, "skip", new Action(ActionType.SHIFT, 18));
        addAction(36, "halt", new Action(ActionType.SHIFT, 19));
        addAction(36, "print", new Action(ActionType.SHIFT, 20));
        addAction(36, "if", new Action(ActionType.SHIFT, 27));
        addAction(36, "F", new Action(ActionType.SHIFT, 28));
        addAction(36, "return", new Action(ActionType.SHIFT, 24));
        addGoto(36, "VNAME", 25);
        addGoto(36, "INSTRUC", 65);
        addGoto(36, "COMMAND", 17);
        addGoto(36, "ASSIGN", 21);
        addGoto(36, "CALL", 22);
        addGoto(36, "BRANCH", 23);
        addGoto(36, "FNAME", 26);
        addAction(37, ";", new Action(ActionType.REDUCE, 11));
        addAction(38, ",", new Action(ActionType.REDUCE, 15));
        addAction(38, ";", new Action(ActionType.REDUCE, 15));
        addAction(38, ")", new Action(ActionType.REDUCE, 15));
        addAction(39, ",", new Action(ActionType.REDUCE, 16));
        addAction(39, ";", new Action(ActionType.REDUCE, 16));
        addAction(39, ")", new Action(ActionType.REDUCE, 16));
        addAction(40, ",", new Action(ActionType.REDUCE, 17));
        addAction(40, ";", new Action(ActionType.REDUCE, 17));
        addAction(40, ")", new Action(ActionType.REDUCE, 17));
        addAction(41, ",", new Action(ActionType.REDUCE, 18));
        addAction(41, ";", new Action(ActionType.REDUCE, 18));
        addAction(41, ")", new Action(ActionType.REDUCE, 18));
        addAction(42, ";", new Action(ActionType.REDUCE, 57));
        addAction(43, ";", new Action(ActionType.REDUCE, 19));
        addAction(44, "V", new Action(ActionType.SHIFT, 9));
        addAction(44, "N", new Action(ActionType.SHIFT, 40));
        addAction(44, "T", new Action(ActionType.SHIFT, 41));
        addAction(44, "not", new Action(ActionType.SHIFT, 59));
        addAction(44, "sqrt", new Action(ActionType.SHIFT, 60));
        addAction(44, "or", new Action(ActionType.SHIFT, 51));
        addAction(44, "and", new Action(ActionType.SHIFT, 52));
        addAction(44, "eq", new Action(ActionType.SHIFT, 53));
        addAction(44, "grt", new Action(ActionType.SHIFT, 54));
        addAction(44, "add", new Action(ActionType.SHIFT, 55));
        addAction(44, "sub", new Action(ActionType.SHIFT, 56));
        addAction(44, "mul", new Action(ActionType.SHIFT, 57));
        addAction(44, "div", new Action(ActionType.SHIFT, 58));
        addAction(44, "F", new Action(ActionType.SHIFT, 28));
        addGoto(44, "VNAME", 38);
        addGoto(44, "ATOMIC", 67);
        addGoto(44, "CONST", 39);
        addGoto(44, "CALL", 68);
        addGoto(44, "TERM", 66);
        addGoto(44, "OP", 69);
        addGoto(44, "UNOP", 70);
        addGoto(44, "BINOP", 71);
        addGoto(44, "FNAME", 26);
        addAction(45, "V", new Action(ActionType.SHIFT, 9));
        addAction(45, "N", new Action(ActionType.SHIFT, 40));
        addAction(45, "T", new Action(ActionType.SHIFT, 41));
        addGoto(45, "VNAME", 38);
        addGoto(45, "ATOMIC", 72);
        addGoto(45, "CONST", 39);
        addAction(46, "then", new Action(ActionType.SHIFT, 73));
        addAction(47, "then", new Action(ActionType.REDUCE, 30));
        addAction(48, "then", new Action(ActionType.REDUCE, 31));
        addAction(49, "(", new Action(ActionType.SHIFT, 74));
        addAction(50, "(", new Action(ActionType.SHIFT, 75));
        addAction(51, "(", new Action(ActionType.REDUCE, 37));
        addAction(52, "(", new Action(ActionType.REDUCE, 38));
        addAction(53, "(", new Action(ActionType.REDUCE, 39));
        addAction(54, "(", new Action(ActionType.REDUCE, 40));
        addAction(55, "(", new Action(ActionType.REDUCE, 41));
        addAction(56, "(", new Action(ActionType.REDUCE, 42));
        addAction(57, "(", new Action(ActionType.REDUCE, 43));
        addAction(58, "(", new Action(ActionType.REDUCE, 44));
        addAction(59, "(", new Action(ActionType.REDUCE, 35));
        addAction(60, "(", new Action(ActionType.REDUCE, 36));
        addAction(61, "begin", new Action(ActionType.REDUCE, 2));
        addAction(62, "begin", new Action(ActionType.SHIFT, 7));
        addGoto(62, "ALGO", 76);
        addAction(63, "V", new Action(ActionType.SHIFT, 9));
        addGoto(63, "VNAME", 77);
        addAction(64, "V", new Action(ActionType.SHIFT, 9));
        addGoto(64, "VNAME", 78);
        addAction(65, "end", new Action(ActionType.REDUCE, 8));
        addAction(66, ";", new Action(ActionType.REDUCE, 20));
        addAction(67, ";", new Action(ActionType.REDUCE, 23));
        addAction(68, ";", new Action(ActionType.REDUCE, 24));
        addAction(69, ";", new Action(ActionType.REDUCE, 25));
        addAction(70, "(", new Action(ActionType.SHIFT, 79));
        addAction(71, "(", new Action(ActionType.SHIFT, 80));
        addAction(72, ",", new Action(ActionType.SHIFT, 81));
        addAction(73, "begin", new Action(ActionType.SHIFT, 7));
        addGoto(73, "ALGO", 82);
        addAction(74, "V", new Action(ActionType.SHIFT, 9));
        addAction(74, "N", new Action(ActionType.SHIFT, 40));
        addAction(74, "T", new Action(ActionType.SHIFT, 41));
        addAction(74, "or", new Action(ActionType.SHIFT, 51));
        addAction(74, "and", new Action(ActionType.SHIFT, 52));
        addAction(74, "eq", new Action(ActionType.SHIFT, 53));
        addAction(74, "grt", new Action(ActionType.SHIFT, 54));
        addAction(74, "add", new Action(ActionType.SHIFT, 55));
        addAction(74, "sub", new Action(ActionType.SHIFT, 56));
        addAction(74, "mul", new Action(ActionType.SHIFT, 57));
        addAction(74, "div", new Action(ActionType.SHIFT, 58));
        addGoto(74, "VNAME", 38);
        addGoto(74, "ATOMIC", 83);
        addGoto(74, "CONST", 39);
        addGoto(74, "SIMPLE", 84);
        addGoto(74, "BINOP", 85);
        addAction(75, "or", new Action(ActionType.SHIFT, 51));
        addAction(75, "and", new Action(ActionType.SHIFT, 52));
        addAction(75, "eq", new Action(ActionType.SHIFT, 53));
        addAction(75, "grt", new Action(ActionType.SHIFT, 54));
        addAction(75, "add", new Action(ActionType.SHIFT, 55));
        addAction(75, "sub", new Action(ActionType.SHIFT, 56));
        addAction(75, "mul", new Action(ActionType.SHIFT, 57));
        addAction(75, "div", new Action(ActionType.SHIFT, 58));
        addGoto(75, "SIMPLE", 86);
        addGoto(75, "BINOP", 85);
        addAction(76, "}", new Action(ActionType.SHIFT, 88));
        addGoto(76, "EPILOG", 87);
        addAction(77, ",", new Action(ActionType.SHIFT, 89));
        addAction(78, ",", new Action(ActionType.SHIFT, 90));
        addAction(79, "V", new Action(ActionType.SHIFT, 9));
        addAction(79, "N", new Action(ActionType.SHIFT, 40));
        addAction(79, "T", new Action(ActionType.SHIFT, 41));
        addAction(79, "not", new Action(ActionType.SHIFT, 59));
        addAction(79, "sqrt", new Action(ActionType.SHIFT, 60));
        addAction(79, "or", new Action(ActionType.SHIFT, 51));
        addAction(79, "and", new Action(ActionType.SHIFT, 52));
        addAction(79, "eq", new Action(ActionType.SHIFT, 53));
        addAction(79, "grt", new Action(ActionType.SHIFT, 54));
        addAction(79, "add", new Action(ActionType.SHIFT, 55));
        addAction(79, "sub", new Action(ActionType.SHIFT, 56));
        addAction(79, "mul", new Action(ActionType.SHIFT, 57));
        addAction(79, "div", new Action(ActionType.SHIFT, 58));
        addGoto(79, "VNAME", 38);
        addGoto(79, "ATOMIC", 92);
        addGoto(79, "CONST", 39);
        addGoto(79, "OP", 93);
        addGoto(79, "ARG", 91);
        addGoto(79, "UNOP", 70);
        addGoto(79, "BINOP", 71);
        addAction(80, "V", new Action(ActionType.SHIFT, 9));
        addAction(80, "N", new Action(ActionType.SHIFT, 40));
        addAction(80, "T", new Action(ActionType.SHIFT, 41));
        addAction(80, "not", new Action(ActionType.SHIFT, 59));
        addAction(80, "sqrt", new Action(ActionType.SHIFT, 60));
        addAction(80, "or", new Action(ActionType.SHIFT, 51));
        addAction(80, "and", new Action(ActionType.SHIFT, 52));
        addAction(80, "eq", new Action(ActionType.SHIFT, 53));
        addAction(80, "grt", new Action(ActionType.SHIFT, 54));
        addAction(80, "add", new Action(ActionType.SHIFT, 55));
        addAction(80, "sub", new Action(ActionType.SHIFT, 56));
        addAction(80, "mul", new Action(ActionType.SHIFT, 57));
        addAction(80, "div", new Action(ActionType.SHIFT, 58));
        addGoto(80, "VNAME", 38);
        addGoto(80, "ATOMIC", 92);
        addGoto(80, "CONST", 39);
        addGoto(80, "OP", 93);
        addGoto(80, "ARG", 94);
        addGoto(80, "UNOP", 70);
        addGoto(80, "BINOP", 71);
        addAction(81, "V", new Action(ActionType.SHIFT, 9));
        addAction(81, "N", new Action(ActionType.SHIFT, 40));
        addAction(81, "T", new Action(ActionType.SHIFT, 41));
        addGoto(81, "VNAME", 38);
        addGoto(81, "ATOMIC", 95);
        addGoto(81, "CONST", 39);
        addAction(82, "else", new Action(ActionType.SHIFT, 96));
        addAction(83, ",", new Action(ActionType.SHIFT, 97));
        addAction(84, ",", new Action(ActionType.SHIFT, 98));
        addAction(85, "(", new Action(ActionType.SHIFT, 99));
        addAction(86, ")", new Action(ActionType.SHIFT, 100));
        addAction(87, "num", new Action(ActionType.SHIFT, 14));
        addAction(87, "end", new Action(ActionType.REDUCE, 46));
        addAction(87, "void", new Action(ActionType.SHIFT, 15));
        addAction(87, "$", new Action(ActionType.REDUCE, 46));
        addGoto(87, "FUNCTIONS", 102);
        addGoto(87, "DECL", 11);
        addGoto(87, "HEADER", 12);
        addGoto(87, "FTYP", 13);
        addGoto(87, "SUBFUNCS", 101);
        addAction(88, "num", new Action(ActionType.REDUCE, 54));
        addAction(88, "end", new Action(ActionType.REDUCE, 54));
        addAction(88, "void", new Action(ActionType.REDUCE, 54));
        addAction(88, "$", new Action(ActionType.REDUCE, 54));
        addAction(89, "num", new Action(ActionType.SHIFT, 4));
        addAction(89, "text", new Action(ActionType.SHIFT, 5));
        addGoto(89, "VTYP", 103);
        addAction(90, "V", new Action(ActionType.SHIFT, 9));
        addGoto(90, "VNAME", 104);
        addAction(91, ")", new Action(ActionType.SHIFT, 105));
        addAction(92, ",", new Action(ActionType.REDUCE, 28));
        addAction(92, ")", new Action(ActionType.REDUCE, 28));
        addAction(93, ",", new Action(ActionType.REDUCE, 29));
        addAction(93, ")", new Action(ActionType.REDUCE, 29));
        addAction(94, ",", new Action(ActionType.SHIFT, 106));
        addAction(95, ",", new Action(ActionType.SHIFT, 107));
        addAction(96, "begin", new Action(ActionType.SHIFT, 7));
        addGoto(96, "ALGO", 108);
        addAction(97, "V", new Action(ActionType.SHIFT, 9));
        addAction(97, "N", new Action(ActionType.SHIFT, 40));
        addAction(97, "T", new Action(ActionType.SHIFT, 41));
        addGoto(97, "VNAME", 38);
        addGoto(97, "ATOMIC", 109);
        addGoto(97, "CONST", 39);
        addAction(98, "or", new Action(ActionType.SHIFT, 51));
        addAction(98, "and", new Action(ActionType.SHIFT, 52));
        addAction(98, "eq", new Action(ActionType.SHIFT, 53));
        addAction(98, "grt", new Action(ActionType.SHIFT, 54));
        addAction(98, "add", new Action(ActionType.SHIFT, 55));
        addAction(98, "sub", new Action(ActionType.SHIFT, 56));
        addAction(98, "mul", new Action(ActionType.SHIFT, 57));
        addAction(98, "div", new Action(ActionType.SHIFT, 58));
        addGoto(98, "SIMPLE", 110);
        addGoto(98, "BINOP", 85);
        addAction(99, "V", new Action(ActionType.SHIFT, 9));
        addAction(99, "N", new Action(ActionType.SHIFT, 40));
        addAction(99, "T", new Action(ActionType.SHIFT, 41));
        addGoto(99, "VNAME", 38);
        addGoto(99, "ATOMIC", 83);
        addGoto(99, "CONST", 39);
        addAction(100, "then", new Action(ActionType.REDUCE, 34));
        addAction(101, "end", new Action(ActionType.SHIFT, 111));
        addAction(102, "end", new Action(ActionType.REDUCE, 56));
        addAction(103, "V", new Action(ActionType.SHIFT, 9));
        addGoto(103, "VNAME", 112);
        addAction(104, ",", new Action(ActionType.SHIFT, 113));
        addAction(105, ",", new Action(ActionType.REDUCE, 26));
        addAction(105, ";", new Action(ActionType.REDUCE, 26));
        addAction(105, ")", new Action(ActionType.REDUCE, 26));
        addAction(106, "V", new Action(ActionType.SHIFT, 9));
        addAction(106, "N", new Action(ActionType.SHIFT, 40));
        addAction(106, "T", new Action(ActionType.SHIFT, 41));
        addAction(106, "not", new Action(ActionType.SHIFT, 59));
        addAction(106, "sqrt", new Action(ActionType.SHIFT, 60));
        addAction(106, "or", new Action(ActionType.SHIFT, 51));
        addAction(106, "and", new Action(ActionType.SHIFT, 52));
        addAction(106, "eq", new Action(ActionType.SHIFT, 53));
        addAction(106, "grt", new Action(ActionType.SHIFT, 54));
        addAction(106, "add", new Action(ActionType.SHIFT, 55));
        addAction(106, "sub", new Action(ActionType.SHIFT, 56));
        addAction(106, "mul", new Action(ActionType.SHIFT, 57));
        addAction(106, "div", new Action(ActionType.SHIFT, 58));
        addGoto(106, "VNAME", 38);
        addGoto(106, "ATOMIC", 92);
        addGoto(106, "CONST", 39);
        addGoto(106, "OP", 93);
        addGoto(106, "ARG", 114);
        addGoto(106, "UNOP", 70);
        addGoto(106, "BINOP", 71);
        addAction(107, "V", new Action(ActionType.SHIFT, 9));
        addAction(107, "N", new Action(ActionType.SHIFT, 40));
        addAction(107, "T", new Action(ActionType.SHIFT, 41));
        addGoto(107, "VNAME", 38);
        addGoto(107, "ATOMIC", 115);
        addGoto(107, "CONST", 39);
        addAction(108, ";", new Action(ActionType.REDUCE, 22));
        addAction(109, ")", new Action(ActionType.SHIFT, 116));
        addAction(110, ")", new Action(ActionType.SHIFT, 117));
        addAction(111, "num", new Action(ActionType.REDUCE, 52));
        addAction(111, "end", new Action(ActionType.REDUCE, 52));
        addAction(111, "void", new Action(ActionType.REDUCE, 52));
        addAction(111, "$", new Action(ActionType.REDUCE, 52));
        addAction(112, ",", new Action(ActionType.SHIFT, 118));
        addAction(113, "V", new Action(ActionType.SHIFT, 9));
        addGoto(113, "VNAME", 119);
        addAction(114, ")", new Action(ActionType.SHIFT, 120));
        addAction(115, ")", new Action(ActionType.SHIFT, 121));
        addAction(116, ",", new Action(ActionType.REDUCE, 32));
        addAction(116, ")", new Action(ActionType.REDUCE, 32));
        addAction(116, "then", new Action(ActionType.REDUCE, 32));
        addAction(117, "then", new Action(ActionType.REDUCE, 33));
        addAction(118, "num", new Action(ActionType.SHIFT, 4));
        addAction(118, "text", new Action(ActionType.SHIFT, 5));
        addGoto(118, "VTYP", 122);
        addAction(119, ")", new Action(ActionType.SHIFT, 123));
        addAction(120, ",", new Action(ActionType.REDUCE, 27));
        addAction(120, ";", new Action(ActionType.REDUCE, 27));
        addAction(120, ")", new Action(ActionType.REDUCE, 27));
        addAction(121, ";", new Action(ActionType.REDUCE, 21));
        addAction(122, "V", new Action(ActionType.SHIFT, 9));
        addGoto(122, "VNAME", 124);
        addAction(123, "{", new Action(ActionType.REDUCE, 49));
        addAction(124, ",", new Action(ActionType.SHIFT, 125));
        addAction(125, "begin", new Action(ActionType.REDUCE, 55));
        addAction(10, "$", new Action(ActionType.ACCEPT));
    }

    // add action to the actions table
    private void addAction(int state, String symbol, Action action) {
        actionTable.computeIfAbsent(state, k -> new HashMap<>()).put(symbol, action);
    }

    // add goto entries in the goto table
    private void addGoto(int state, String symbol, int nextState) {
        gotoTable.computeIfAbsent(state, k -> new HashMap<>()).put(symbol, nextState);
    }

    // retrieve action from action table
    // a returned value of null implies that there is no action for a state cross-indexed with a symbol
    public Action getAction(int state, Token symbolToken) {
        String symbol = null;
        
        switch(symbolToken.getTokenClass()){
            
            case RESERVED_KEYWORD: {
                symbol = symbolToken.getTokenWord();
                break;
            }
            
            case VARIABLE: {
                symbol = "V";
                break;
            }
            
            case TEXT: {
                symbol = "T";
                break;
            }
            
            case USER_DEFINED_FUNCTION: {
                symbol = "F";
                break;
            }
            
            case NUMBER: {
                symbol = "N";
                break;
            }

            case END_OF_INPUT: {
                symbol = "$";
                break;
            }
        }
        return actionTable.getOrDefault(state, new HashMap<>()).get(symbol);
    }
    
    // only meant for displayTablesToFile() function 
    private Action getAction(int state, String symbol) {
        return actionTable.getOrDefault(state, new HashMap<>()).get(symbol);
    }

    // retrieve (next) state from goto table
    // a returned value of null implies that there is no state returned for a state cross-indexed with a symbol
    public Integer getGoto(int state, String grammarVariable) {
        return gotoTable.getOrDefault(state, new HashMap<>()).get(grammarVariable);
    }
    
    public void displayTablesToFile(String fileName) {
        String[] terminals = {"main", ",", "num", "text", "V", "begin", "end", ";", "skip", "halt", "print", "return", "N", "T", "<input", "=", "(", ")", "if", "then", "else", "not", "sqrt", "or", "and", "eq", "grt", "add", "sub", "mul", "div", "F", "void", "{", "}", "$"};
        String[] nonTerminals = {"PROG", "GLOBVARS", "VTYP", "VNAME", "ALGO", "INSTRUC", "COMMAND", "ATOMIC", "CONST", "ASSIGN", "CALL", "BRANCH", "TERM", "OP", "ARG", "COND", "SIMPLE", "COMPOSIT", "UNOP", "BINOP", "FNAME", "FUNCTIONS", "DECL", "HEADER", "FTYP", "BODY", "PROLOG", "EPILOG", "LOCVARS", "SUBFUNCS"};
        
        try (FileWriter writer = new FileWriter(fileName)) {
            
            // Write number of states
            int numStates = Math.max(actionTable.size(), gotoTable.size());
            writer.write("The parse table has " + numStates + " states\n");

            // Write the headers (state, terminals, non-terminals)
            writer.write(String.format("%-12s", "State"));
            for (String t : terminals) {
                writer.write(String.format("%-12s", t));
            }
            for (String nt : nonTerminals) {
                writer.write(String.format("%-12s", nt));
            }
            writer.write("\n");


            // Write each row of the table
            for (int state = 0; state <= numStates; state++) {
                writer.write(String.format("%-12s", state));

                // Write action table (terminals)
                for (String t : terminals) {
                    Action action = getAction(state, t);
                    if (action != null) {
                        writer.write(String.format("%-12s", action.toString()));
                    } else {
                        writer.write(String.format("%-12s", ""));
                    }
                }

                // Write goto table (non-terminals)
                for (String nt : nonTerminals) {
                    Integer nextState = getGoto(state, nt);
                    if (nextState != null) {
                        writer.write(String.format("%-12s", nextState));
                    } else {
                        writer.write(String.format("%-12s", ""));
                    }
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}