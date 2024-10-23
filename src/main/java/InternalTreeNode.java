import java.util.ArrayList;
import java.util.List;

public class InternalTreeNode implements ASTNode {
    private String grammarVariable; // Name of the grammar variable (non-terminal)
    private List<ASTNode> children;
    private ASTNode parent;

    public InternalTreeNode(String grammarVariable) {
        this.children = new ArrayList<>();
        this.grammarVariable = grammarVariable;
        this.parent = null;
    }

    @Override
    public void addChild(ASTNode child) {
        children.add(child);
        child.setParent(this);
    }

    @Override
    public List<ASTNode> getChildren() {
        return children;
    }

    @Override
    public String getValue() {
        return grammarVariable;
    }

    
    @Override
    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    @Override
    public ASTNode getParent() {
       return this.parent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(grammarVariable);
        if (!children.isEmpty()) {
            sb.append("\n");
            for (ASTNode child : children) {
                String childString = child.toString().replaceAll("(?m)^", "    ");
                sb.append("├── ").append(childString).append("\n");
            }
            sb.setLength(sb.length() - 1);  // Remove the last newline character
        }
        return sb.toString();
    }
}
