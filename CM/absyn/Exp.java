package absyn;

public abstract class Exp extends Absyn {
    // The type of the expression, which will be retrieved by looking up its
    // terms' types in a symbol table.  
    public NameTy dtype = null;
}
