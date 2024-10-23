import java.util.List;

public interface ASTNode {
    void addChild(ASTNode child);
    List<ASTNode> getChildren();
    String toString();
    String getValue();  // New method to retrieve the value of the node
}
