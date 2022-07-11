package absyn;

public class IfExp extends Exp {
    
    public Exp testExp;
    public Exp thenExp;
    public Exp elseExp;

    public IfExp(int row, int col, Exp testExp, Exp thenExp, Exp elseExp) {
        this.row = row;
        this.col = col;
        this.testExp = testExp;
        this.thenExp = thenExp;
        this.elseExp = elseExp;
    }

    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);  
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }
}
