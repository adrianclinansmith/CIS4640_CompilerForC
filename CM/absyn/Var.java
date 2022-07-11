package absyn;

public abstract class Var extends Absyn {
    // The name of the variable
    public String name;
    // The declaration type of the variable, which will be retrieved
    // from a symbol table. If no declaration is found, it will be null. 
    public NameTy dtype = null;
    // For the assembly code generator
    public VarDec dec;
}
