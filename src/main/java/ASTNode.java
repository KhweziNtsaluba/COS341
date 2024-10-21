import java.util.ArrayList;
import java.util.List;

class ASTNode {
    String type;  // Type of the node (non-terminal or token type)
    List<ASTNode> children;  // Children of this node

    public ASTNode(String type) {
        this.type = type;
        this.children = new ArrayList<>();
    }

    public void addChild(ASTNode child) {
        children.add(child);
    }

    @Override
    public String toString() {
        return type + " -> " + children;
    }
}
