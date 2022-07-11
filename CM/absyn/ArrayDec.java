package absyn;

public class ArrayDec extends VarDec {
    
    public IntExp size = null;

    private int typeErrorNum = 0;
    private int sizeErrorNum = 0;

    // ************************************************************************
    // Constructors
    // ************************************************************************

    public ArrayDec(int row, int col, NameTy type, String name, String size) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.name = name;
        if (size != null) {
            this.size = new IntExp(size);
        }
    }

    // ************************************************************************
    // Public Methods
    // ************************************************************************

    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);
    }

    @Override
    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }

    // for CP3
    public boolean isArrayPtr() {
        return size == null;
    }

    @Override 
    public String semanticString() {
        String sizeStr = this.size != null ? this.size+"" : "";
        return this.name+"["+sizeStr+"]: "+this.type; 
    }

    @Override
    public String toString() {
        String typeStr = this.type != null ? this.type+"" : "";
        String sizeStr = this.size != null ? this.size+"" : "";
        if (this.typeErrorNum > 0) {
            typeStr = this.errorToken(this.typeErrorNum);
        }
        if (this.sizeErrorNum > 0) {
            sizeStr = this.errorToken(this.sizeErrorNum);
        }
        return typeStr+" "+name+"["+sizeStr+"]";
    }

    // ************************************************************************
    // Error Constructors
    // ************************************************************************

    public static ArrayDec withTypeError(int row, int col, String name,
    String size) {
        ArrayDec arrDec = new ArrayDec(row, col, null, name, size);
        String errorMessage = "Invalid array declaration. ";
        errorMessage += "Type must be 'int' or 'void'.";
        arrDec.typeErrorNum = arrDec.reportError(errorMessage);
        return arrDec;
    }

    public static ArrayDec withSizeError(int row, int col, NameTy type,
    String name) {
        ArrayDec arrDec = new ArrayDec(row, col, type, name, null);
        String errorMessage = "Invalid array declaration. ";
        errorMessage += "Size must be a simple integer.";
        arrDec.sizeErrorNum = arrDec.reportError(errorMessage);
        return arrDec;
    }
}
