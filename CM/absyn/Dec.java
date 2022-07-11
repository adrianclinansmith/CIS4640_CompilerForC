package absyn;

public abstract class Dec extends Absyn { 
    
    public NameTy type;
    public String name;

    public String semanticString() {
        return this.name + ": " + this.type; 
    }

    @Override
    public String toString() {
        return this.type + " " + this.name;
    }
}
