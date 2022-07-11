package absyn;

public class OpExp extends Exp {

    public static final int PLUS = 0; 
    public static final int MINUS = 1; 
    public static final int TIMES = 2;
    public static final int DIVIDE = 3;
    public static final int LESS = 4;
    public static final int LESS_EQ = 5;
    public static final int GREATER = 6;
    public static final int GREATER_EQ = 7;
    public static final int EQUAL = 8;
    public static final int NOT_EQUAL = 9;

    public Exp left;
    public Exp right;
    public int op;

    public OpExp(int row, int col, Exp left, int op, Exp right) {
        this.row = row;
        this.col = col;
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);
    }

    @Override
    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }

    public boolean isArithmetic() {
        return op <= DIVIDE;
    }

    @Override
    public String toString() {
        switch (this.op) {
            case PLUS:          return "+";
            case MINUS:         return "-";
            case TIMES:         return "*";
            case DIVIDE:        return "/";
            case LESS:          return "<";
            case LESS_EQ:       return "<=";
            case GREATER:       return ">";
            case GREATER_EQ:    return ">=";
            case EQUAL:         return "==";
            case NOT_EQUAL:     return "!=";
            default:            return "unknown";
        }
    }
}
