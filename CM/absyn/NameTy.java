package absyn;

public class NameTy extends Absyn {

    public static final int INT  = 0;
    public static final int VOID = 1;
    
    public int type;
    /* 
        Is an array type. This is only used during semantic analysis and is set
        by the semantic analyzer. 
    */ 
    public boolean array;

    public NameTy(int row, int col, int type) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.array = false;
    }

    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }

    public boolean isInt() {
        return this.type == NameTy.INT;
    }

    public boolean isVoid() {
        return this.type == NameTy.VOID;
    }

    public boolean sameAs(NameTy other) {
        return this.type == other.type && this.array == other.array;
    }

    @Override
    public String toString() {
        if (type == NameTy.INT) {
            return "int";
        }
        return "void";
    }
}
