package absyn;

public class VarDecList extends Absyn {

    public VarDec head;
    public VarDecList tail;

    /* Constructors */

    public VarDecList(VarDec head, VarDecList tail) {
        this.head = head;
        this.tail = tail;
    }

    public VarDecList(VarDec head) {
        this.head = head;
        this.tail = null;
    }

    /* Methods */

    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);
    }

    @Override
    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }

    public VarDecList append(VarDec dec) {
        VarDecList t = this;
        while(t.tail != null) {
            t = t.tail;
        }
        t.tail = new VarDecList(dec, null);
        return this;
    } 

    public int size() {
        int n = 0;
        VarDecList t = this;
        while(t != null) {
            n += t.head.getSize();
            t = t.tail;
        }
        return n;
    }
}
