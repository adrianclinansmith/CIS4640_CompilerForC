package absyn;

public class IntExp extends Exp {

    public int value;

    public IntExp(String value) {
        this.value = Integer.parseInt(value);
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
        return this.value + "";
    }
}
