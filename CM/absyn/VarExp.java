package absyn;

public class VarExp extends Exp {
    
    public Var variable;

    public VarExp(int row, int col, Var variable) {
        this.row = row;
        this.col = col;
        this.variable = variable;
    }

    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);
    }

    @Override
    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }
}
