package absyn;

public class CallExp extends Exp {

    public String func;
    public ExpList args;
    // For the assembly code generator
    public FunctionDec dec;

    public CallExp(int row, int col, String func, ExpList args) {
        this.row = row;
        this.col = col;
        this.func = func;
        this.args = args;
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
        return this.func + "()";
    }

    public ExpList getArgs() {
        if (args != null) {
            return args.tail;
        }
        return null;
    }
}
