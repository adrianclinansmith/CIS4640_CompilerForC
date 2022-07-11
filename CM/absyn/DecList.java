package absyn;

public class DecList extends Absyn {
    
    public Dec head;
    public DecList tail;

    public DecList(Dec head, DecList tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);
    }

    @Override
    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }

    public void append(Dec dec) {
        DecList t = this;
        while(t.tail != null) {
            t = t.tail;
        }
        t.tail = new DecList(dec, null);
    }
}
