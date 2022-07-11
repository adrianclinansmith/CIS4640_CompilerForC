package absyn;

public class ReturnExp extends Exp {

    public Exp exp;

    public ReturnExp(int row, int col, Exp exp) {
        this.row = row;
        this.col = col;
        this.exp = exp;
    }

    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }

    @Override
    public String toString() {
        return "return";
    }
}
