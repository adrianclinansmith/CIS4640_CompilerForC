/* 
    ShowTreeVisitor
    By: Adrian Clinansmith
    March 2022
*/
import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

    static final int SPACES = 4;

    private void indent(int level) {
        for(int i = 0; i < level * SPACES; i++) {
            System.out.print(" ");
        }
    }

    private void printIndentedLine(String toPrint, int level) {
        indent(level);
        System.out.println(toPrint);
    }

    // **************************************************************************
    // Visit methods
    // **************************************************************************

    /* Types */

    public void visit(NameTy nameType, int level, boolean isAddr) {
        // will not get called
    }

    /* Variables */

    public void visit(SimpleVar simpleVar, int level, boolean isAddr) {
        printIndentedLine("var: " + simpleVar, level);
    }

    public void visit(IndexVar indexVar, int level, boolean isAddr) {
        printIndentedLine("var: " + indexVar, level);
        level++;
        indexVar.index.accept(this, level);
    }

    /* Expressions */

    public void visit(NilExp exp, int level, boolean isAddr) {
        // Nothing to display
    }

    public void visit(VarExp varExp, int level, boolean isAddr) {
        varExp.variable.accept(this, level);
    }

    public void visit(IntExp num, int level, boolean isAddr) {
        printIndentedLine("num: " + num, level);
    }

    public void visit(CallExp funCall, int level, boolean isAddr) {
        printIndentedLine("call: " + funCall, level);
        if (funCall.args != null) {
            level++;
            funCall.args.accept(this, level);
        }
    }

    public void visit(OpExp op, int level, boolean isAddr) {
        printIndentedLine("OpExp: " + op, level);
        level++;
        op.left.accept(this, level);
        op.right.accept(this, level);
    }

    public void visit(AssignExp assign, int level, boolean isAddr) {
        printIndentedLine("Assign:", level);
        level++;
        assign.lhs.accept(this, level);
        assign.rhs.accept(this, level);
    }

    public void visit(IfExp ifExp, int level, boolean isAddr) {
        printIndentedLine("if:", level);
        level++;
        ifExp.testExp.accept(this, level);
        ifExp.thenExp.accept(this, level);
        if (ifExp.elseExp != null) {
            ifExp.elseExp.accept(this, level); 
        }
    }

    public void visit(WhileExp whileLoop, int level, boolean isAddr) {
        printIndentedLine("while:", level);
        level++;
        whileLoop.test.accept(this, level);
        whileLoop.body.accept(this, level);
    }

    public void visit(ReturnExp ret, int level, boolean isAddr) {
        printIndentedLine(ret + ":", level);
        if (ret.exp != null) {
            level++;
            ret.exp.accept(this, level);
        }
    }

    public void visit(CompoundExp compound, int level, boolean isAddr) {
        printIndentedLine("Block:", level);
        level++;
        if (compound.decs != null) {
            compound.decs.accept(this, level);
        }
        if (compound.exps != null) {
            compound.exps.accept(this, level);
        }
    }

    /* Declarations */

    public void visit(ArrayDec dec, int level, boolean isAddr) {
        printIndentedLine("VarDec: " + dec, level);
    }

    public void visit(SimpleDec dec, int level, boolean isAddr) {
        printIndentedLine("VarDec: " + dec, level);
    }

    public void visit(FunctionDec func, int level, boolean isAddr) {
        printIndentedLine("FunctionDec: " + func, level);
        level++;
        if (func.paramErrorNum > 0) {
            printIndentedLine(func.errorToken(func.paramErrorNum), level);
        } else if (func.params != null) {
            func.params.accept(this, level);
        }
        func.body.accept(this, level);
    }

    /* Lists */

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
