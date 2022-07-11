package absyn;

import java.util.ArrayList;

public abstract class Absyn {

    public static final ArrayList<String> errors = new ArrayList<>();

    public int row;
    public int col;

    public abstract void accept(AbsynVisitor visitor, int level, boolean isAddr);

    public void accept(AbsynVisitor visitor, int level) {
        accept(visitor, level, false);
    }

    // ************************************************************************
    // Error Handling
    // ************************************************************************

    public String errorToken(int number) {
        return "ERROR<" + number + ">";
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public void outputErrors() {
        if (errors.isEmpty()) {
            return;
        }
        System.err.println("parsing errors:");
        for (String errorMessage : errors) {
            System.err.println(errorMessage);
        }
    }

    public int reportError(String message) {
        String error = (row+1)+":"+(col+1)+"\t"+message;
        errors.add(error);
        return errors.size();
    }
}
