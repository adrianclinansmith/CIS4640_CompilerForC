/* 
    Semantic Analyzer
    By: Adrian Clinansmith
    March 2022
*/
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import absyn.*;

public class CodeGenerator implements AbsynVisitor {

    // *~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~
    // iMem locations 
    private int emitLoc = 0; // next instruction location    
    private int highEmitLoc = 0;
    private int mainEntry = 0; // address of main declaration
    // dMem locations
    private int globalOffset = 0;
    // frame (fp) offsets
    private static final int retOffset = -1; // return value for pc  
    private static final int initialFrameOffset = -2;
    // Register only instructions
    private enum RR {
        HALT, IN, OUT, ADD, SUB, MUL, DIV;
    }
    // Register memory instructions
    private enum RM {
        LD, LDA, LDC, ST, JLT, JLE, JGT, JGE, JEQ, JNE;
    }
    // Registers
    private static final int ac0 = 0;
    private static final int ac1 = 1;
    private static final int ac2 = 2;
    private static final int ac3 = 3;
    private static final int sp = 4;
    private static final int fp = 5;
    private static final int gp = 6;
    private static final int pc = 7;
    // *~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~

    public PrintWriter printWriter;

    private static final int GLOBAL_SCOPE = 1;

    // Code generator
    private static final int GLOBAL = 0;
    private static final int LOCAL = 1;
    private static final int NIL = 0;
    private boolean inLocal = false;

    /* Constructor */

    CodeGenerator(String filename) {
        try {
            if (filename == null) {
                printWriter = new PrintWriter(System.out);
            } else {
                printWriter = new PrintWriter(new FileWriter(filename));
            }
        } catch (Exception e) {
            System.err.println("error creating file " + filename);
        }
    }

    // *~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~
    // Emit methods 
    // *~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~

    private void output(String contents) {
        try {
            printWriter.println(contents);
        } catch (Exception e) {
            System.err.println("error writing to file");
            System.exit(1);
        }
    }

    private void emitComment(String comment) {
        output("* " + comment);
    }

    private void emitRR(RR rr, int r1, int r2, int r3, String comment) {
        String op;
        switch (rr) {
            case IN:  op = "IN";    break;
            case OUT: op = "OUT";   break;
            case ADD: op = "ADD";   break;
            case SUB: op = "SUB";   break;
            case MUL: op = "MUL";   break;
            case DIV: op = "DIV";   break;
            default:  op = "HALT";  break;
        }
        String fmt = "%3d: %5s %d, %d, %d\t%s";
        String code = String.format(fmt, emitLoc, op, r1, r2, r3, comment);
        output(code);
        emitLoc++;
    }

    private void emitRM(RM rm, int r1, int c, int r2, String comment) {
        String op;
        switch (rm) {
            case LD:  op = "LD";    break;
            case LDA: op = "LDA";   break;
            case LDC: op = "LDC";   break;
            case ST:  op = "ST";    break;
            case JLT: op = "JLT";   break;
            case JLE: op = "JLE";   break;
            case JGT: op = "JGT";   break;
            case JGE: op = "JGE";   break;
            case JEQ: op = "JEQ";   break;
            default:  op = "JNE";   break;
        }
        String fmt = "%3d: %5s %d, %d(%d)\t%s";
        String code = String.format(fmt, emitLoc, op, r1, c, r2, comment);
        output(code);
        emitLoc++;
    }

    RM jumpRmForTest(OpExp testExp) {
        switch (testExp.op) {
            case OpExp.LESS:        return RM.JLT;
            case OpExp.LESS_EQ:     return RM.JLE; 
            case OpExp.GREATER:     return RM.JGT; 
            case OpExp.GREATER_EQ:  return RM.JGE; 
            case OpExp.EQUAL:       return RM.JEQ; 
            default:                return RM.JNE;
        }
    }

    // *~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~
    // Public methods 
    // *~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~

    public void generate(Absyn ast) {
        // prelude
        emitComment("prelude:");
        emitRM(RM.LD, gp, 0, ac0, "load MAX_ADDRESS to gp");
        emitRM(RM.LDA, fp, 0, gp, "load MAX_ADDRESS to fp");
        emitRM(RM.ST, ac0, 0, ac0, "clear address 0");
        emitComment("input routine:");
        emitRM(RM.LDA, pc, 7, pc, "jump over i/o code");
        emitRM(RM.ST, ac0, retOffset, fp, "store return"); 
        emitRR(RR.IN, ac0, 0, 0, "input"); 
        emitRM(RM.LD, pc, retOffset, fp, "return to caller");
        emitComment("output routine:");
        emitRM(RM.ST, ac0, retOffset, fp, "store return"); 
        emitRM(RM.LD, ac0, retOffset-1, fp, "load output value"); 
        emitRR(RR.OUT, ac0, 0, 0, "output");
        emitRM(RM.LD, pc, retOffset, fp, "return to caller"); 
        // visit tree
        ast.accept(this, 0);
        // finale
        emitComment("finale:");
        emitRM(RM.ST, fp, globalOffset, fp, "push ofp");
        emitRM(RM.LDA, fp, globalOffset, fp, "push frame"); 
        emitRM(RM.LDA, ac0, 1, pc, "load ac with ret ptr"); 
        emitRM(RM.LDC, pc, mainEntry, 0, "jump to main loc");
        emitRM(RM.LD, fp, 0, fp, "pop frame"); 
        emitRR(RR.HALT, 0, 0, 0, "");
        printWriter.close();
    }

    // ************************************************************************
    // Type Visits
    // ************************************************************************

    //
    public void visit(NameTy nameType, int level, boolean isAddr) {
        // will not get called
    }

    // ************************************************************************
    // Variable Visits
    // ************************************************************************

    public void visit(SimpleVar sVar, int level, boolean isAddr) {
        int pt = sVar.dec.nestLevel == LOCAL ? fp : gp;
        int offset = sVar.dec.offset;
        isAddr = isAddr || ((sVar.dec instanceof ArrayDec) && 
                 !((ArrayDec)sVar.dec).isArrayPtr());
        if (isAddr) {
            emitRM(RM.LDA, ac0, offset, pt, "load var '"+sVar+"' addr to ac");
            emitRM(RM.ST, ac0, level, fp, "store '"+sVar+"' addr in memory");
        } else {
            emitRM(RM.LD, ac0, offset, pt, "load var '"+sVar+"' to ac");
            emitRM(RM.ST, ac0, level, fp, "store '"+sVar+"' in memory");
        }
    }

    public void visit(IndexVar indexVar, int level, boolean isAddr) {
        indexVar.index.accept(this, level);
        // index out of bounds
        if (!((ArrayDec)indexVar.dec).isArrayPtr()) {
            int size = indexVar.dec.getSize();
            emitRM(RM.LDC, ac1, size, NIL, "load the array's size in ac1");
            emitRR(RR.SUB, ac1, ac1, ac0, "load 'size - index' into ac1");
            emitRM(RM.JGT, ac1, 1, pc, "jump over halt if index < size");
            emitRR(RR.HALT, 1, 0, 0, "halt 1: index bigger than size");
            emitRM(RM.JGE, ac0, 1, pc, "jump over halt if index ≥ 0");
            emitRR(RR.HALT, 2, 0, 0, "halt 2: index less than zero");
        }
        // *~*~*~*~*~*~*~*~*~*
        int pt = indexVar.dec.nestLevel == LOCAL ? fp : gp;
        int offset = indexVar.dec.offset;
        if (((ArrayDec)indexVar.dec).isArrayPtr()) {
            emitRM(RM.LD, ac1, offset, fp, "load arr '"+indexVar+"' addr to ac1");
            emitRR(RR.SUB, ac1, ac1, ac0, "ac1 := ac1 - ac0");
            offset= 0;
        } else {
            emitRR(RR.SUB, ac1, pt, ac0, "ac1 := pointer - ac0");
        }
        if (isAddr) {
            emitRM(RM.LDA, ac0, offset, ac1, "load arr '"+indexVar+"' addr to ac");
            emitRM(RM.ST, ac0, level, fp, "store '"+indexVar+"' addr in memory");
        } else {
            emitRM(RM.LD, ac0, offset, ac1, "load arr '"+indexVar+"' to ac");
            emitRM(RM.ST, ac0, level, fp, "store '"+indexVar+"' in memory");
        }
    }

    // **************************************************************************
    // Expression Visits
    // **************************************************************************

    //
    public void visit(NilExp nil, int level, boolean isAddr) {
        nil.dtype = new NameTy(nil.row, nil.col, NameTy.VOID);
    }

    public void visit(VarExp varExp, int level, boolean isAddr) {
        varExp.variable.accept(this, level);
    }

    public void visit(IntExp num, int level, boolean isAddr) {
        int val = num.value;
        emitRM(RM.LDC, ac0, val, NIL, "load integer " + val + " to ac");
        emitRM(RM.ST, ac0, level, fp, "store integer " + val + " in memory");
    }

    public void visit(CallExp call, int level, boolean isAddr) {
        emitComment("- call to " + call.func);
        ExpList arg = call.args;
        VarDecList param = call.dec.params;
        while (arg != null) { 
            int offset = param.head.offset + level;
            arg.head.accept(this, offset);
            emitRM(RM.ST, ac0, offset, fp, "store argument in frame");
            arg = arg.tail;
            param = param.tail;
        }
        int address = call.dec.funAddr;
        emitRM(RM.ST, fp, level, fp, "store caller's fp");
        emitRM(RM.LDA, fp, level, fp, "push new frame"); 
        emitRM(RM.LDA, ac0, 1, pc, "save return loc in ac"); 
        emitRM(RM.LDC, pc, address, NIL, "jump to function entry"); 
        emitRM(RM.LD, fp, 0, fp, "pop current frame");
        if (call.dec.type.isInt()) {
            emitRM(RM.ST, ac0, level, fp, "store return val for " + call.func);
        }
        emitComment("- finished call to " + call.func);
    }

    public void visit(OpExp op, int level, boolean isAddr) {
        op.left.accept(this, level);
        op.right.accept(this, level-1);
        emitRM(RM.LD, ac0, level, fp, "load LHS of '" + op + "' to ac0");
        emitRM(RM.LD, ac1, level-1, fp, "load RHS of '" + op + "' to ac1");
        if (op.isArithmetic()) {
            RR rrOp;
            switch (op.op) {
                case OpExp.PLUS:  rrOp = RR.ADD; break;
                case OpExp.MINUS: rrOp = RR.SUB; break;
                case OpExp.TIMES: rrOp = RR.MUL; break;
                default:          rrOp = RR.DIV; break;
            }
            emitRR(rrOp, ac0, ac0, ac1, "perform '" + op + "' and load to ac0");
            emitRM(RM.ST, ac0, level, fp, "store ac0 in memory");
        } else {
            emitRR(RR.SUB, ac0, ac0, ac1, "perform LHS-RHS and store in ac0");
        }
    }

    public void visit(AssignExp assign, int level, boolean isAddr) {
        assign.lhs.accept(this, level, true);
        assign.rhs.accept(this, level-1);
        emitRM(RM.LD, ac0, level, fp, "load the address to assign into ac0");
        emitRM(RM.LD, ac1, level-1, fp, "load the assignment's RHS into ac1");
        emitRM(RM.ST, ac1, 0, ac0, "store assign's RHS to the var location");
        emitRM(RM.ST, ac1, level, fp, "store assign's RHS to memory");
    }

    public void visit(IfExp ifExp, int level, boolean isAddr) {
        emitComment("- if start");
        ifExp.testExp.accept(this, level);
        emitRM(RM.ST, ac0, level, fp, "store test result in memory"); 
        int jumpLoc = emitLoc;
        emitLoc += 3;
        ifExp.thenExp.accept(this, level);
        int elseJumpLoc = emitLoc;
        int bodyLen = emitLoc - 3 - jumpLoc/* + 1*/;
        bodyLen += ifExp.elseExp != null ? 1 : 0;
        emitLoc = jumpLoc;
        RM op = jumpRmForTest((OpExp)ifExp.testExp);
        emitRM(RM.LD, ac0, level, fp, "load the test result in ac0");
        emitRM(op, ac0, 1, pc, "enter if-body if ac0 "+ifExp.testExp+" 0");
        emitRM(RM.LDA, pc, bodyLen, pc, "jump over if-body");
        emitLoc = bodyLen + jumpLoc + 3;
        if (ifExp.elseExp != null) {
            emitComment("- else start");
            ifExp.elseExp.accept(this, level); 
            int elseLen = emitLoc - 1 - elseJumpLoc;
            emitLoc = elseJumpLoc;
            emitRM(RM.LDA, pc, elseLen, pc, "jump over else");
            emitLoc = elseLen + elseJumpLoc + 1;
        }
        emitComment("- if end");
    }

    public void visit(WhileExp whileLoop, int level, boolean isAddr) {
        emitComment("- while loop start");
        int testLoc = emitLoc;
        whileLoop.test.accept(this, level);
        emitRM(RM.ST, ac0, level, fp, "store test result in memory"); 
        int jumpLoc = emitLoc;
        emitLoc += 3;
        whileLoop.body.accept(this, level-1);
        emitRM(RM.LDC, pc, testLoc, NIL, "jump back to while test");
        int bodyLen = emitLoc - 3 - jumpLoc;
        emitLoc = jumpLoc;
        RM op = jumpRmForTest((OpExp)whileLoop.test);
        emitRM(RM.LD, ac0, level, fp, "load the test result in ac0");
        emitRM(op, ac0, 1, pc, "enter while body if ac0 "+whileLoop.test+" 0");
        emitRM(RM.LDA, pc, bodyLen, pc, "jump over while loop");
        emitLoc = bodyLen + jumpLoc + 3;
        emitComment("- while loop end");
    }

    public void visit(ReturnExp ret, int level, boolean isAddr) {
        ret.exp.accept(this, level);
    }

    public void visit(CompoundExp compound, int level, boolean isAddr) {
        if (compound.decs != null) {
            compound.decs.accept(this, level);
            level -= compound.decs.size() /*+ 1*/;
        }
        if (compound.exps != null) {
            compound.exps.accept(this, level);
        }
    }

    // ************************************************************************
    // Declaration Visits
    // ************************************************************************

    public void visit(ArrayDec dec, int level, boolean isAddr) {
        if (inLocal) {
            dec.nestLevel = LOCAL;
        } else {
            dec.nestLevel = GLOBAL;
            int offset = -dec.getSize();
            emitRM(RM.LDA, fp, offset, fp, "move fp below global " + dec);
        }
        dec.offset = level;
    }

    public void visit(SimpleDec dec, int level, boolean isAddr) {
        if (inLocal) {
            dec.nestLevel = LOCAL;
        } else {
            dec.nestLevel = GLOBAL;
            int offset = -dec.getSize();
            emitRM(RM.LDA, fp, offset, fp, "move fp below global " + dec);
        }
        dec.offset = level;
    }

    // 0
    public void visit(FunctionDec func, int level, boolean isAddr) {
        inLocal = true;
        int jumpLoc = emitLoc;
        emitLoc++;
        if (func.name.equals("main")) {
            mainEntry = emitLoc;
        }
        func.funAddr = emitLoc;
        emitComment(func.name + " routine:");
        emitRM(RM.ST, ac0, retOffset, fp, "store return"); 
        int frameOffset = initialFrameOffset;
        if (func.params != null) {
            func.params.accept(this, frameOffset);
            frameOffset -= func.paramSize(); /*+ 1*/
        }
        func.body.accept(this, frameOffset);
        emitRM(RM.LD, pc, retOffset, fp, "return to caller"); 
        int funcLen = emitLoc - 1 - jumpLoc;
        emitLoc = jumpLoc;
        emitRM(RM.LDA, pc, funcLen, pc, "jump over " + func.name);
        emitLoc = funcLen + jumpLoc + 1;
        inLocal = false;
    }

    // **************************************************************************
    // List Visits
    // **************************************************************************

    public void visit(DecList decList, int level, boolean isAddr) {
        while(decList != null) {
            decList.head.accept(this, level);
            if (decList.head instanceof VarDec) {
                level -= ((VarDec)decList.head).getSize(); // increment initial fp
            }
            decList = decList.tail;
        } 
    }

    public void visit(VarDecList decList, int level, boolean isAddr) {
        while(decList != null) {
            decList.head.accept(this, level);
            level -= decList.head.getSize();   // next frame offset
            decList = decList.tail;
        } 
    }

    //
    public void visit(ExpList expList, int level, boolean isAddr) {
        while(expList != null) {
            expList.head.accept(this, level);
            expList = expList.tail;
        } 
    }
}
