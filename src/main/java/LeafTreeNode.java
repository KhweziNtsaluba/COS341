import java.util.List;

public class LeafTreeNode implements ASTNode {
    private Token token;  // Leaf nodes contain a Token

    public LeafTreeNode(Token token) {
        super();
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        return token.getTokenWord();
    }

    @Override
    public void addChild(ASTNode child) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addChild'");
    }

    @Override
    public List<ASTNode> getChildren() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getChildren'");
    }
}
