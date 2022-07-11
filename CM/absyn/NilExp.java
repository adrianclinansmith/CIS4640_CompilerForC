package absyn;

public class NilExp extends Exp {

    public NilExp(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }
}
