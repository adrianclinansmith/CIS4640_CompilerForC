package absyn;

public class FunctionDec extends Dec {

    public int paramErrorNum = 0;
    public int funAddr; // Code Generator: iMem address

    public VarDecList params;   // parameters
    public CompoundExp body;    // function body

    /* Constructors */

    public FunctionDec(int row, int col, NameTy type, String name,
    VarDecList params, CompoundExp body) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;

    }

    public FunctionDec(int row, int col, NameTy type, String name,
    VarDecList params) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = new CompoundExp(null, null);
    }

    /* Predefined Function Constructors */

    public static FunctionDec inputFunction() {
        NameTy intTy = new NameTy(0, 0, NameTy.INT);
        FunctionDec inputDec =  new FunctionDec(0, 0, intTy, "input", null);
        inputDec.funAddr = 4;
        return inputDec;
    }    

    public static FunctionDec outputFunction() {
        NameTy intTy = new NameTy(0, 0, NameTy.INT);
        SimpleDec param = new SimpleDec(0, 0, intTy, "x");
        param.offset = -2;
        VarDecList pars = new VarDecList(param);
        NameTy voidTy = new NameTy(0, 0, NameTy.VOID);
        FunctionDec outputDec =  new FunctionDec(0, 0, voidTy, "output", pars);
        outputDec.funAddr = 7;
        return outputDec;
    }

    /* Error Constructors */

    public static FunctionDec withParamError(int row, int col, NameTy type, 
    String name, CompoundExp body) {
        FunctionDec funDec = new FunctionDec(row, col, type, name, null, body);
        String errorMessage = "Invalid function declaration. ";
        errorMessage += "Parameters must be 'void' or variable declarations.";
        funDec.paramErrorNum = funDec.reportError(errorMessage);
        return funDec;
    }

    /* Public Methods */

    @Override
    public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
        visitor.visit(this, level, isAddr);
    }

    @Override
    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level, false);
    }

    public int paramSize() {
        return params != null ? params.size() : 0;
    }

    @Override 
    public String semanticString() {
        String paramString = "";
        boolean firstParam = true;
        VarDecList p = params;
        while (p != null) {
            if (!firstParam) {
                paramString += ", ";
            }
            firstParam = false;
            paramString += p.head.type;
            paramString += p.head.type.array ? "[]" : "";
            p = p.tail;
        }
        return this.name + ": ("+paramString+") -> " + this.type; 
    }

    @Override
    public String toString() {
        return super.toString() + "()";
    }

    public boolean validArgs(ExpList args) {
        VarDecList p = params;
        ExpList a = args;
        while (p != null && a != null) {
            if (!p.head.type.sameAs(a.head.dtype)) {
                return false;
            }
            p = p.tail;
            a = a.tail;
        }
        return p == null && a == null;
    }
}
