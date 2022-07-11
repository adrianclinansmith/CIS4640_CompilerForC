package absyn;

public abstract class VarDec extends Dec {
    public int offset; // Code Generator: location relative to fp
    public int nestLevel; // Code Generator: gp or the current fp

    // CP3
    public int getSize() {
        if (!(this instanceof ArrayDec) || ((ArrayDec)this).size == null) {
            return 1;
        }
        return ((ArrayDec)this).size.value;
    }
}
