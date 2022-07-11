package absyn;

public class WhileExp extends Exp {

    public Exp test;
    public Exp body;

    public WhileExp(int row, int col, Exp test, Exp body) {
        this.row = row;
        this.col = col;
        this.test = test;
        this.body = body;
    }
    
    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }
}
