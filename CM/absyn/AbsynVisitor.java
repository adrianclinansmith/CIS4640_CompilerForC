package absyn;

public interface AbsynVisitor {

      /* Types */
      
      public void visit(NameTy type, int level, boolean isAddr);

      /* Variables */

      public void visit(SimpleVar variable, int level, boolean isAddr);
      public void visit(IndexVar variable, int level, boolean isAddr);

      /* Expressions */

      public void visit(NilExp exp, int level, boolean isAddr);
      public void visit(VarExp exp, int level, boolean isAddr);
      public void visit(IntExp exp, int level, boolean isAddr);
      public void visit(CallExp exp, int level, boolean isAddr);
      public void visit(OpExp exp, int level, boolean isAddr);
      public void visit(AssignExp exp, int level, boolean isAddr);
      public void visit(IfExp exp, int level, boolean isAddr);
      public void visit(WhileExp exp, int level, boolean isAddr);
      public void visit(ReturnExp exp, int level, boolean isAddr);
      public void visit(CompoundExp exp, int level, boolean isAddr);

      /* Declarations */

      public void visit(SimpleDec dec, int level, boolean isAddr);
      public void visit(ArrayDec dec, int level, boolean isAddr);
      public void visit(FunctionDec dec, int level, boolean isAddr);

      /* Lists */

      public void visit(DecList decList, int level, boolean isAddr);
      public void visit(VarDecList decList, int level, boolean isAddr);
      public void visit(ExpList expList, int level, boolean isAddr);
}
