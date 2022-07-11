/* 
    Semantic Analyzer
    By: Adrian Clinansmith
    March 2022
*/
import java.util.ArrayList;

import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {

    public final SymTable table;
    public final ArrayList<String> errors;

    private static final int SPACES = 4;
    private static final int GLOBAL_SCOPE = 1;

    private FunctionDec inFunction = null;
    private boolean didReturn = false;
    private boolean foundMain = false;
    private boolean showOutput = false;

    /* Constructor */

    SemanticAnalyzer() {
        this.table = new SymTable();
        this.errors = new ArrayList<>();
    }

    /* Public Methods */

    public boolean analyze(Absyn ast, boolean showOutput) {
        // Pre-insert the default function 'int input(void)'
        table.insert(FunctionDec.inputFunction(), GLOBAL_SCOPE);
        // Pre-insert the default function 'void output(int x)'
        table.insert(FunctionDec.outputFunction(), GLOBAL_SCOPE);
        // Initiate the analysis
        this.showOutput = showOutput;
        printIndentedLine("Entering the global scope:", 0);
        ast.accept(this, GLOBAL_SCOPE);
        if (!foundMain) {
            recordError(0, 0, "no main function");
        }
        deleteFromTableAndPrint(GLOBAL_SCOPE);
        printIndentedLine("Leaving the global scope", 0);
        return errors.isEmpty();
    }

    /* Private Methods */

    private void analyzeMain(FunctionDec mainFunc) {
        foundMain = true;
        if (!mainFunc.type.isVoid() || mainFunc.params != null) {
            String err = "main must be 'void main(void)'";
            recordError(mainFunc.row, mainFunc.col, err);
        }
    }

    private void indent(int level) {
        for(int i = 0; i < level * SPACES; i++) {
            System.out.print(" ");
        }
    }

    private boolean isInt(Exp exp) {
        return exp.dtype.type == NameTy.INT;
    }

    private boolean isInt(Var variable) {
        return variable.dtype.type == NameTy.INT;
    }

    private void printIndentedLine(String toPrint, int level) {
        if (showOutput) {
            indent(level);
            System.out.println(toPrint);
        }
    }

    private void deleteFromTableAndPrint(int level) {
        ArrayList<Dec> deleted = this.table.delete(level);
        for (Dec dec : deleted) {
            printIndentedLine(dec.semanticString(), level);
        }
    }

    private void recordError(int row, int col, String msg) {
        String error = (row + 1) + ":" + (col + 1) + "\t" + msg;
        this.errors.add(error);
    }

    // ************************************************************************
    // Type Visits
    // ************************************************************************

    //
    public void visit(NameTy nameType, int level, boolean isAddr) {
        // will not get called
    }

    // **************************************************************************
    // Variable Visits
    // **************************************************************************

    public void visit(SimpleVar simpleVar, int level, boolean isAddr) {
        String name = simpleVar.name;
        int row = simpleVar.row;
        int col = simpleVar.col;
        simpleVar.dtype = new NameTy(row, col, NameTy.INT);
        Dec dec = this.table.lookup(name);
        if (dec == null) {
            recordError(row, col, "'"+name+"' undeclared");
        } else if (dec instanceof FunctionDec) {
            recordError(row, col, "'"+name+"' used as variable");
        } else if (dec instanceof ArrayDec) {
            simpleVar.dtype.array = true;
        }
        // For CP3: code generation
        if (dec instanceof VarDec) {
            simpleVar.dec = (VarDec)dec;
        }
    }

    public void visit(IndexVar indexVar, int level, boolean isAddr) {
        String name = indexVar.name;
        int row = indexVar.row;
        int col = indexVar.col;
        Dec dec = this.table.lookup(name);
        if (dec == null) {
            recordError(row, col, "'"+name+"' undeclared");
        } else if (!(dec instanceof ArrayDec)) {
            recordError(row, col, "'"+name+"' used as an array");
        } 
        indexVar.index.accept(this, level);
        if (!indexVar.index.dtype.isInt()) {
            String err = "void used as index";
            recordError(indexVar.index.row, indexVar.index.row, err);
        }
        indexVar.dtype = new NameTy(row, col, NameTy.INT); 
        // For CP3: code generation
        if (dec instanceof VarDec) {
            indexVar.dec = (VarDec)dec;
        }
    }

    // **************************************************************************
    // Expression Visits
    // **************************************************************************

    public void visit(NilExp nil, int level, boolean isAddr) {
        nil.dtype = new NameTy(nil.row, nil.col, NameTy.VOID);
    }

    public void visit(VarExp varExp, int level, boolean isAddr) {
        varExp.variable.accept(this, level);
        varExp.dtype = varExp.variable.dtype;
    }

    public void visit(IntExp num, int level, boolean isAddr) {
        num.dtype = new NameTy(num.row, num.col, NameTy.INT);
    }

    public void visit(CallExp call, int level, boolean isAddr) {
        if (call.args != null) {
            call.args.accept(this, level);
        }
        String name = call.func;
        int row = call.row;
        int col = call.col;
        Dec dec = table.lookup(name);
        if (dec == null) {
            recordError(row, col, "undeclared function '"+name+"'");
            call.dtype = new NameTy(row, col, NameTy.INT);
        } else if (!(dec instanceof FunctionDec)) {
            recordError(row, col, "called '"+name+"' as a function");
            call.dtype = new NameTy(row, col, NameTy.INT);
        } else if (!((FunctionDec)dec).validArgs(call.args)) {
            recordError(row, col, "mismatched arguments for '"+name+"'");
            call.dtype = dec.type;
        } else {
            call.dtype = dec.type;
        } 
        // For CP3: code generation
        if (dec instanceof FunctionDec) {
            call.dec = (FunctionDec)dec;
        }
    }

    public void visit(OpExp op, int level, boolean isAddr) {
        op.left.accept(this, level);
        op.right.accept(this, level);
        String voidErr = "void type used in '"+op+"' operation";
        String arrayErr = "array used in '"+op+"' operation";
        if (!isInt(op.left)) {
            recordError(op.left.row, op.left.col, voidErr);
        } else if (op.left.dtype.array) {
            recordError(op.left.row, op.left.col, arrayErr);
        }
        if (!isInt(op.right)) {
            recordError(op.right.row, op.right.col, voidErr);
        } else if (op.right.dtype.array) {
            recordError(op.right.row, op.right.col, arrayErr);
        }
        op.dtype = new NameTy(op.row, op.col, NameTy.INT);
    }

    public void visit(AssignExp assign, int level, boolean isAddr) {
        assign.lhs.accept(this, level);
        assign.rhs.accept(this, level);
        if (!isInt(assign.lhs)) {
            String err = "assignment to void '"+assign.lhs.name+"'";
            recordError(assign.lhs.row, assign.lhs.col, err);
        } else if (assign.lhs.dtype.array) {
            String err = "assignment to array '"+assign.lhs.name+"'";
            recordError(assign.lhs.row, assign.lhs.col, err);
        }
        if (!isInt(assign.rhs)) {
            recordError(assign.rhs.row, assign.rhs.col, "void assignment");
        } else if (assign.rhs.dtype.array) {
            recordError(assign.rhs.row, assign.rhs.col, "array assignment");
        }
        assign.dtype = new NameTy(assign.row, assign.col, NameTy.INT);
    }

    public void visit(IfExp ifExp, int level, boolean isAddr) {
        ifExp.testExp.accept(this, level);
        if (ifExp.testExp.dtype.isVoid()) {
            String err = "void used in if condition";
            recordError(ifExp.testExp.row, ifExp.testExp.col, err);
        }
        ifExp.thenExp.accept(this, level);
        if (ifExp.elseExp != null) {
            ifExp.elseExp.accept(this, level); 
        }
    }

    public void visit(WhileExp whileLoop, int level, boolean isAddr) {
        whileLoop.test.accept(this, level);
        if (whileLoop.test.dtype.isVoid()) {
            String err = "void used in while condition";
            recordError(whileLoop.test.row, whileLoop.test.col, err);
        }
        whileLoop.body.accept(this, level);
    }

    public void visit(ReturnExp ret, int level, boolean isAddr) {
        didReturn = true;
        ret.exp.accept(this, level);
        ret.dtype = ret.exp.dtype;
        if (!ret.dtype.sameAs(this.inFunction.type)) {
            String funName = this.inFunction.name;
            String err = "wrong return type for function '"+funName+"'";
            recordError(ret.row, ret.col,  err);
        }
    }

    public void visit(CompoundExp compound, int level, boolean isAddr) {
        String block = "Entering a new block:";
        String func = "Entering the scope for function "+inFunction.name+":";
        printIndentedLine(level == GLOBAL_SCOPE ? func : block, level);
        level++;
        if (compound.decs != null) {
            compound.decs.accept(this, level);
        }
        if (compound.exps != null) {
            compound.exps.accept(this, level);
        }
        deleteFromTableAndPrint(level);
        level--;
        block = "Leaving the block";
        func = "Leaving the function scope";
        printIndentedLine(level == GLOBAL_SCOPE ? func : block, level);
    }

    // ************************************************************************
    // Declaration Visits
    // ************************************************************************

    public void visit(ArrayDec dec, int level, boolean isAddr) {
        if (foundMain && level == GLOBAL_SCOPE) {
            String err = "declaration '"+dec.name+"' after 'main'";
            recordError(dec.row, dec.col, err);
        }
        if (!dec.type.isInt()) {
            String err = "array '" + dec.name + "' declared void";
            this.recordError(dec.row, dec.col, err);
            dec.type = new NameTy(dec.type.row, dec.type.col, NameTy.INT);
        }
        dec.type.array = true;
        boolean didInsert = this.table.insert(dec, level);
        if (!didInsert) {
            String err = "redefinition of '" + dec.name + "'";
            this.recordError(dec.row, dec.col, err);
        }
    }

    public void visit(SimpleDec dec, int level, boolean isAddr) {
        if (foundMain && level == GLOBAL_SCOPE) {
            String err = "declaration '"+dec.name+"' after 'main'";
            recordError(dec.row, dec.col, err);
        }
        if (dec.type.type != NameTy.INT) {
            String err = "variable '" + dec.name + "' declared void";
            this.recordError(dec.row, dec.col, err);
            dec.type = new NameTy(dec.type.row, dec.type.col, NameTy.INT);
        }
        boolean didInsert = this.table.insert(dec, level);
        if (!didInsert) {
            String err = "redefinition of '" + dec.name + "'";
            this.recordError(dec.row, dec.col, err);
        }
    }

    public void visit(FunctionDec funDec, int level, boolean isAddr) {
        inFunction = funDec;
        if (funDec.name.equals("main")) {
            analyzeMain(funDec);
        } else if (foundMain) {
            String err = "declaration '"+funDec.name+"' after 'main'";
            recordError(funDec.row, funDec.col, err);
        }
        boolean didInsert = table.insert(funDec, level);
        if (!didInsert) {
            String err = "redefinition of function '"+funDec.name+"'";
            recordError(funDec.row, funDec.col, err);
        }
        if (funDec.params != null) {
            funDec.params.accept(this, level+1);
        }
        funDec.body.accept(this, level);
        if (!didReturn && !funDec.type.isVoid()) {
            String err = "no return for non-void function '"+funDec.name+"'";
            recordError(funDec.row, funDec.col, err);
        }
        didReturn = false;
        inFunction = null;
    }

    // **************************************************************************
    // List Visits
    // **************************************************************************

    public void visit(DecList decList, int level, boolean isAddr) {
        while(decList != null) {
            decList.head.accept(this, level);
            decList = decList.tail;
        } 
    }

    public void visit(VarDecList decList, int level, boolean isAddr) {
        while(decList != null) {
            decList.head.accept(this, level);
            decList = decList.tail;
        } 
    }

    public void visit(ExpList expList, int level, boolean isAddr) {
        while(expList != null) {
            expList.head.accept(this, level);
            expList = expList.tail;
        } 
    }
}
