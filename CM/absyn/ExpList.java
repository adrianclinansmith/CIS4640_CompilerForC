package absyn;

public class ExpList {
   
    public Exp head;
    public ExpList tail;

    public ExpList(Exp head, ExpList tail) {
        this.head = head;
        this.tail = tail;
    }

    public ExpList(Exp head) {
        this.head = head;
        this.tail = null;
    }

    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }

    public ExpList append(Exp exp) {
        ExpList t = this;
        while(t.tail != null) {
            t = t.tail;
        }
        t.tail = new ExpList(exp);
        return this;
    }
}    
