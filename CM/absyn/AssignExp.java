package absyn;

public class AssignExp extends Exp {
    
    public Var lhs;
    public Exp rhs;

    public AssignExp(int row, int col, Var lhs, Exp rhs) {
        this.row = row;
        this.col = col;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);  
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }
}
