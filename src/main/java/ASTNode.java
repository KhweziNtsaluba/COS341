import java.util.ArrayList;
import java.util.List;

public class ASTNode {
    private String value;
    private List<ASTNode> children;

    public ASTNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(ASTNode child) {
        children.add(child);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(value);
        if (!children.isEmpty()) {
            sb.append(" -> ").append(children);
        }
        return sb.toString();
    }
}
