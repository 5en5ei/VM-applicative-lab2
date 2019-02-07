package fr.umlv.smalljs.stackinterp;

import java.util.Objects;

public class Code {
  private final int[] instrs;
  private final int parameterCount;
  private final int localVarCount;

  public Code(int[] instrs, int parameterCount, int localVarCount) {
    this.instrs = Objects.requireNonNull(instrs);
    this.parameterCount = Objects.requireNonNull(parameterCount);
    this.localVarCount = Objects.requireNonNull(localVarCount);
  }
  
  public int[] getInstrs() {
    return instrs;
  }
  public int getParameterCount() {
    return parameterCount;
  }
  public int getLocalVarCount() {
    return localVarCount;
  }

  @Override
  public String toString() {
    return "ins:" + instrs.length + " params:" + parameterCount + " locals:" + localVarCount;
  }
}