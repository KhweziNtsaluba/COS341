import java.util.ArrayList;
import java.util.List;

public interface ASTNode {
    public void addChild(ASTNode child);
    public String toString();
    public List<ASTNode> getChildren();
}