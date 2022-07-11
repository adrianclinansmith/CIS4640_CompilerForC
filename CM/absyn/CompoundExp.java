package absyn;

public class CompoundExp extends Exp {

    public VarDecList decs;
    public ExpList exps;

    public CompoundExp(VarDecList decs, ExpList exps) {
        this.decs = decs;
        this.exps = exps;
    }

    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }
}