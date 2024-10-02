import java.util.HashMap;
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

        addAction(0, "(", new Action(ActionType.SHIFT, 4));
        addAction(0, "id", new Action(ActionType.SHIFT, 5));
        
        addAction(1, "+", new Action(ActionType.SHIFT, 6));
        addAction(1, "$", new Action(ActionType.ACCEPT));
        
        addAction(2, "+", new Action(ActionType.REDUCE, 2));
        addAction(2, "*", new Action(ActionType.SHIFT, 7));
        addAction(2, ")", new Action(ActionType.REDUCE, 2));
        addAction(2, "$", new Action(ActionType.REDUCE, 2));
        
        addAction(3, "+", new Action(ActionType.REDUCE, 4));
        addAction(3, "*", new Action(ActionType.REDUCE, 4));
        addAction(3, ")", new Action(ActionType.REDUCE, 4));
        addAction(3, "$", new Action(ActionType.REDUCE, 4));
        
        addAction(4, "(", new Action(ActionType.SHIFT, 4));
        addAction(4, "id", new Action(ActionType.SHIFT, 5));
        
        addAction(5, "+", new Action(ActionType.REDUCE, 6));
        addAction(5, "*", new Action(ActionType.REDUCE, 6));
        addAction(5, ")", new Action(ActionType.REDUCE, 6));
        addAction(5, "$", new Action(ActionType.REDUCE, 6));
        
        addAction(6, "(", new Action(ActionType.SHIFT, 4));
        addAction(6, "id", new Action(ActionType.SHIFT, 5));
        
        addAction(7, "(", new Action(ActionType.SHIFT, 4));
        addAction(7, "id", new Action(ActionType.SHIFT, 5));
        
        addAction(8, "+", new Action(ActionType.SHIFT, 6));
        addAction(8, ")", new Action(ActionType.SHIFT, 11));
        
        addAction(9, "+", new Action(ActionType.REDUCE, 1));
        addAction(9, "*", new Action(ActionType.SHIFT, 7));
        addAction(9, ")", new Action(ActionType.REDUCE, 1));
        addAction(9, "$", new Action(ActionType.REDUCE, 1));
        
        addAction(10, "+", new Action(ActionType.REDUCE, 3));
        addAction(10, "*", new Action(ActionType.REDUCE, 3));
        addAction(10, ")", new Action(ActionType.REDUCE, 3));
        addAction(10, "$", new Action(ActionType.REDUCE, 3));
        
        addAction(11, "+", new Action(ActionType.REDUCE, 5));
        addAction(11, "*", new Action(ActionType.REDUCE, 5));
        addAction(11, ")", new Action(ActionType.REDUCE, 5));
        addAction(11, "$", new Action(ActionType.REDUCE, 5));
        
        // GOTO table setup
        addGoto(0, "E'", 1);
        addGoto(0, "E", 2);
        addGoto(0, "T", 3);
        addGoto(0, "F", 3);
        
        addGoto(4, "E", 8);
        addGoto(4, "T", 2);
        addGoto(4, "F", 3);
        
        addGoto(6, "T", 9);
        addGoto(6, "F", 3);
        
        addGoto(7, "F", 10);
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
    public Action getAction(int state, String symbol) {
        return actionTable.getOrDefault(state, new HashMap<>()).get(symbol);
    }

    // retrieve (next) state from goto table
    // a returned value of null implies that there is no state returned for a state cross-indexed with a symbol
    public Integer getGoto(int state, String symbol) {
        return gotoTable.getOrDefault(state, new HashMap<>()).get(symbol);
    }

    public void displayTables() {
        String[] terminals = {"+", "*", "(", ")", "id", "$"};
        String[] nonTerminals = {"E'", "E", "T", "F"};

        System.out.printf("%-8s", "State");
        for (String t : terminals) {
            System.out.printf("%-8s", t);
        }
        for (String nt : nonTerminals) {
            System.out.printf("%-8s", nt);
        }
        System.out.println();

        int numStates = Math.max(actionTable.size(), gotoTable.size());
        System.out.println("The parse table has " + numStates + " states");

        for (int state = 0; state < numStates; state++) {
            System.out.printf("%-8s", state);

            // Print action table (terminals)
            for (String t : terminals) {
                Action action = getAction(state, t);
                if (action != null) {
                    System.out.printf("%-8s", action.toString());
                } else {
                    System.out.printf("%-8s", "");
                }
            }

            // Print go table (non-terminals)
            for (String nt : nonTerminals) {
                Integer nextState = getGoto(state, nt);
                if (nextState != null) {
                    System.out.printf("%-8s", nextState);
                } else {
                    System.out.printf("%-8s", "");
                }
            }
            System.out.println();
        }
    }
}
