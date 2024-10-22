import java.util.Hashtable;

public class SymbolTable {
    Hashtable<String,EntryData> symboHashtable; // name, information pair

    private void bind(String name,information){}
    private information lookup(String name){}

    // Inner symbol table information class
    class EntryData {
        final float ID;
        String type; // type of the variable
        String nameType; // function or variable
        final String internalName;
    }
}
