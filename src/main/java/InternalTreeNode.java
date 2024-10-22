import java.util.ArrayList;
import java.util.List;

public class InternalTreeNode implements ASTNode {
    private String grammarVariable; // Name of the grammar variable (non-terminal)
    private List<ASTNode> children; // children of node

    public InternalTreeNode(String grammarVariable) {
        this.children = new ArrayList<>();
        this.grammarVariable = grammarVariable;
    }

    @Override
    public void addChild(ASTNode child){
        children.add(child);
    }

    @Override
    public List<ASTNode> getChildren() {
        return this.children;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(grammarVariable);
        if (!getChildren().isEmpty()) {
            sb.append("\n");
            for (ASTNode child : getChildren()) {
                String childString = child.toString().replaceAll("(?m)^", "    "); // Indent child nodes
                sb.append("├── ").append(childString).append("\n");
            }
            // Remove the last newline character
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

}
