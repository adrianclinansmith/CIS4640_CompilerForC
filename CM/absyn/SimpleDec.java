package absyn;

public class SimpleDec extends VarDec {

    public int errorNum = 0;

    /* Regular constructors */

    public SimpleDec(int row, int col, NameTy type, String name) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.name = name;
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
    
    @Override
    public String toString() {
        if (this.errorNum > 0) {
            return this.errorToken(this.errorNum);
        }
        return super.toString();
    }

    /* Error constructors */

    public static SimpleDec withError(int row, int col) {
        SimpleDec dec = new SimpleDec(row, col, null, null);
        dec.errorNum = dec.reportError("Invalid variable declaration.");
        return dec;
    }
}
