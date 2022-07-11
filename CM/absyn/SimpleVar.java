package absyn;

public class SimpleVar extends Var {
    
    public SimpleVar(int row, int col, String name) {
        this.row = row;
        this.col = col;
        this.name = name;
    }

    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);  
    }

    @Override
    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
