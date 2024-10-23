import java.util.List;

public class LeafTreeNode implements ASTNode {
    private Token token;  // Leaf nodes contain a Token
    private ASTNode parent;

    public LeafTreeNode(Token token) {
        super();
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String getValue() {
        return token.getTokenWord();
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
    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    @Override
    public ASTNode getParent() {
       return this.parent;
    }

    @Override
    public List<ASTNode> getChildren() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getChildren'");
    }
}
