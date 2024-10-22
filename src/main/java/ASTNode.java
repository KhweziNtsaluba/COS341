import java.util.ArrayList;
import java.util.List;

public class ASTNode {
    private String value;  // For storing constant values like "2" or "3"
    private List<ASTNode> children;

    public ASTNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    // Add a child node
    public void addChild(ASTNode child) {
        this.children.add(child);
    }


    // Getter for children
    public List<ASTNode> getChildren() {
        return this.children;
    }

    // Method to set the value for constants or other value nodes
    public void setValue(String value) {
        this.value = value;
    }

    // Getter for value (optional, if needed in type checking or other operations)
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        // A simple string representation of the node
        StringBuilder sb = new StringBuilder();
        if (value != null) {
            sb.append(" (").append(value).append(")");
        }
        return sb.toString();
    }
}
