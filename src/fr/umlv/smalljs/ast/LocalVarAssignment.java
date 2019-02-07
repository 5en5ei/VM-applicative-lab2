package fr.umlv.smalljs.ast;

import static java.util.Objects.requireNonNull;

public class LocalVarAssignment extends AbstractExpr implements Instr {

  private String name;
  private Expr expr;
  private boolean declaration;

  LocalVarAssignment(String name, Expr expr, boolean declaration, int lineNumber) {
    super(lineNumber);
    this.name = requireNonNull(name);
    this.expr = requireNonNull(expr);
    this.declaration = declaration;
  }
  
  public String getName() {
    return name;
  }
  public Expr getExpr() {
    return expr;
  }
  public boolean isDeclaration() {
    return declaration;
  }
}
