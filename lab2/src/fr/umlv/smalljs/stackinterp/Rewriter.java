package fr.umlv.smalljs.stackinterp;

import static fr.umlv.smalljs.stackinterp.Instructions.*;
import static fr.umlv.smalljs.stackinterp.TagValues.encodeObject;
import static fr.umlv.smalljs.stackinterp.TagValues.encodeSmallInt;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import fr.umlv.smalljs.ast.Block;
import fr.umlv.smalljs.ast.Expr;
import fr.umlv.smalljs.ast.Fun;
import fr.umlv.smalljs.ast.FunCall;
import fr.umlv.smalljs.ast.If;
import fr.umlv.smalljs.ast.Literal;
import fr.umlv.smalljs.ast.LocalVarAccess;
import fr.umlv.smalljs.ast.LocalVarAssignment;
import fr.umlv.smalljs.ast.Return;
import fr.umlv.smalljs.ast.VoidVisitor;
import fr.umlv.smalljs.rt.JSObject;

public class Rewriter {
  static class InstrBuffer {
    private int[] instrs;
    private int size;

    InstrBuffer() {
      instrs = new int[32];
    }

    InstrBuffer emit(int value) {
      if (size == instrs.length) {
        instrs = Arrays.copyOf(instrs, size << 1);
      }
      instrs[size++] = value;
      return this;
    }

    int label() {
      return size;
    }
    int placeholder() {
      return size++;
    }
    void patch(int position, int label) {
      instrs[position] = label;
    }

    int[] toInstrs() {
      return Arrays.copyOf(instrs, size);
    }
  }

  private final Dictionary dict;
  private final InstrBuffer buffer;
  private final JSObject globalEnv;

  private Rewriter(Dictionary dict, InstrBuffer buffer, JSObject globalEnv) {
    this.dict = dict;
    this.buffer = buffer;
    this.globalEnv = globalEnv;
    this.visitor = createVisitor(buffer, dict, globalEnv);
  }

  public static JSObject createFunction(Optional<String> name, List<String> parameters, Block body, Dictionary dict, JSObject globalEnv) {
    JSObject env = JSObject.newEnv(null);

    for(String parameter: parameters) {
      env.register(parameter, env.length());
    }
    visitVariable(body, env);

    InstrBuffer buffer = new InstrBuffer();
    Rewriter rewriter = new Rewriter(dict, buffer, globalEnv);
    rewriter.rewrite(body, env);
    buffer.emit(CONST).emit(encodeObject(JSObject.UNDEFINED, dict));
    buffer.emit(RET);

    int[] instrs = buffer.toInstrs();
    Instructions.dump(instrs, dict);

    Code code = new Code(instrs, parameters.size(), env.length() - parameters.size());
    JSObject function = JSObject.newFunction(name.orElse("<anonymous>"), (self, receiver, args) -> 
            StackInterpreter.execute(self, dict, globalEnv));
    function.register("__code__", code);
    return function;
  }

  private static void visitVariable(Expr expr, JSObject env) {
    VARIABLE_VISITOR.visit(expr, env);
  }

  private static final VoidVisitor<JSObject> VARIABLE_VISITOR =
    new VoidVisitor<JSObject>()
    .when(Block.class, (block, env) -> {
      for(Expr instr: block.getInstrs()) {
        visitVariable(instr, env);
      }
    })
    .when(Literal.class, (literal, env) -> {
      // do nothing
    })
    .when(FunCall.class, (funCall, env) -> {
      // do nothing
    })
    .when(LocalVarAssignment.class, (localVarAssignment, env) -> {
      if (localVarAssignment.isDeclaration()) {
        env.register(localVarAssignment.getName(), env.length());
      }
    })
    .when(LocalVarAccess.class, (localVarAccess, env) -> {
      // do nothing
    })
    .when(Fun.class, (fun, env) -> {
      // do nothing
    })
    .when(Return.class, (_return, env) -> {
      // do nothing
    })
    .when(If.class, (_if, env) -> {
      visitVariable(_if.getTrueBlock(), env);
      visitVariable(_if.getFalseBlock(), env);
    });

  private void rewrite(Expr expr, JSObject env) {
    visitor.visit(expr, env);
  }

  public static VoidVisitor<JSObject> createVisitor(InstrBuffer buffer, Dictionary dict, JSObject globalEnv) {
    VoidVisitor<JSObject> visitor = new VoidVisitor<>();
    visitor.when(Block.class, (block, env) -> {
      for(Expr e: block.getInstrs()) {
        visitor.visit(e, env);
        buffer.emit(POP);
      }
    })
    .when(Literal.class, (literal, env) -> {
      Object value = literal.getValue();
      if (value instanceof Integer) {
        buffer.emit(CONST).emit(encodeSmallInt((Integer)value));
      } else {
        buffer.emit(CONST).emit(encodeObject(value, dict));
      }
    })
    .when(FunCall.class, (funCall, env) -> {
      throw new UnsupportedOperationException("TODO FunCall");
    })
    .when(LocalVarAssignment.class, (localVarAssignment, env) -> {
      throw new UnsupportedOperationException("TODO LocalVarAssignment");
    })
    .when(LocalVarAccess.class, (localVarAccess, env) -> {
      throw new UnsupportedOperationException("TODO LocalVarAccess");
    })
    .when(Fun.class, (fun, env) -> {
      throw new UnsupportedOperationException("TODO Fun");
    })
    .when(Return.class, (_return, env) -> {
      throw new UnsupportedOperationException("TODO Return");
    })
    .when(If.class, (_if, env) -> {
      throw new UnsupportedOperationException("TODO If");
    })
    ;
    return visitor;
  }

  private final VoidVisitor<JSObject> visitor;
}
