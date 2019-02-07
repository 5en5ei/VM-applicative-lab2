package fr.umlv.smalljs.stackinterp;

import static fr.umlv.smalljs.rt.JSObject.UNDEFINED;
import static fr.umlv.smalljs.stackinterp.TagValues.decodeAnyValue;
import static fr.umlv.smalljs.stackinterp.TagValues.decodeObject;
import static fr.umlv.smalljs.stackinterp.TagValues.decodeSmallInt;
import static fr.umlv.smalljs.stackinterp.TagValues.encodeAnyValue;
import static fr.umlv.smalljs.stackinterp.TagValues.encodeObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.umlv.smalljs.ast.Block;
import fr.umlv.smalljs.ast.Script;
import fr.umlv.smalljs.rt.JSObject;

public class StackInterpreter {
  private static void push(int[] stack, int sp, int value) {
    stack[sp] = value;
  }
  
  private static int pop(int[] stack, int sp) {
    return stack[sp];
  }
  
  private static int peek(int[] stack, int sp) {
    return stack[sp - 1];
  }
  
  private static void store(int[] stack, int bp, int offset, int value) {
    stack[bp + offset] = value;
  }
  
  private static int load(int[] stack, int bp, int offset) {
    return stack[bp + offset];
  }
  
  private static void dumpStack(int[] stack, int sp, int bp, Dictionary dict) {
    for(int i = sp - 1; i >= 0; i = i -1) {
      int value = stack[i];
      try {
        System.out.println(((i == bp)? "->" : "  ") + value + " " + decodeAnyValue(value, dict));
      } catch(IndexOutOfBoundsException e) {
        System.out.println(((i == bp)? "->" : "  ") + value);
      }
    }
    System.out.println();
  }
  
  private static final int BP_OFFSET = 0;
  private static final int PC_OFFSET = 1;
  private static final int FUN_OFFSET = 2;
  private static final int ACTIVATION_SIZE = 3;

  public static Object execute(JSObject function, Dictionary dict, JSObject globalEnv) {
    int[] stack = new int[/*64*/ 4096];
    Code code = (Code)function.lookup("__code__");
    int[] instrs = code.getInstrs();
    
    int pc = 0;  // instruction pointer
    int bp = 0;  // base pointer
    int sp = bp + code.getLocalVarCount() + ACTIVATION_SIZE;  // stack pointer
    
    for(;;) {
      switch(instrs[pc++]) {
      case Instructions.CONST:
        push(stack, sp++, instrs[pc++]);
        continue;
      case Instructions.LOOKUP: {
        throw new UnsupportedOperationException("TODO LOOKUP");
      }
      case Instructions.REGISTER: {
        throw new UnsupportedOperationException("TODO REGISTER");
      }
      case Instructions.LOAD:
        throw new UnsupportedOperationException("TODO LOAD");
      case Instructions.STORE: {
        throw new UnsupportedOperationException("TODO STORE");
      }
      case Instructions.DUP: {
        throw new UnsupportedOperationException("TODO DUP");
      }
      case Instructions.POP:
        throw new UnsupportedOperationException("TODO POP");
      case Instructions.FUNCALL: {
        throw new UnsupportedOperationException("TODO FUNCALL");
        /*
        //DEBUG
        //dumpStack(stack, sp, bp, dict);

        JSObject oldFunction = function;
        int argumentCount = instrs[pc++];
        function = null;
        Object asCode = function.lookup("__code__");
        if (asCode == UNDEFINED) {  // native call !
          // call function.invoke() with the right arguments
          
          // remove the arguments (and the function) from the stack
          
          // push the result on the stack
          
          //DEBUG
          //dumpStack(stack, sp, bp, dict);
          continue;
        }
        code = (Code)asCode;

        // initialize locals (erase needless arguments)

        // save bp/pc/code in activation zone

        // use the instructions of the new function
        instrs = code.getInstrs();
        pc = 0;
        bp = 0;
        sp = 0;

        //DEBUG
        //dumpStack(stack, sp, bp, dict);
        continue;
        */
      }
      case Instructions.RET: {
        throw new UnsupportedOperationException("TODO RET");
        /*
        //DEBUG
        //dumpStack(stack, sp, bp, dict);
        
        // get the return value
        int result = 0;

        // restore bp, sp, pc from activation

        if (pc == 0) {
          // end of the interpreter
          return decodeAnyValue(result, dict);
        }

        // restore function, code and instrs

        // remove old function reference

        // push result

        //DEBUG
        //dumpStack(stack, sp, bp, dict);
        continue;
        */
      }
      case Instructions.GOTO:
        throw new UnsupportedOperationException("TODO GOTO");
      case Instructions.JUMP_IF_TRUE: {
        throw new UnsupportedOperationException("TODO JUMP_IF_TRUE");
      }
      case Instructions.JUMP_IF_FALSE: {
        throw new UnsupportedOperationException("TODO JUMP_IF_FALSE");
      }
      
      case Instructions.PRINT: {
        int value = pop(stack, --sp);
        System.out.println(decodeAnyValue(value, dict));
        continue;
      }
      default:
        throw new Error("unknown instruction " + instrs[pc - 1]);
      }
    }
  }
  
  public static void interpret(Script script) {
    JSObject globalEnv = JSObject.newEnv(null);
    Block body = script.getBody();
    globalEnv.register("global", globalEnv);
    globalEnv.register("print", JSObject.newFunction("print", (self, receiver, args) -> {
      System.out.println(Arrays.stream(args).map(Object::toString).collect(Collectors.joining(" ")));
      return UNDEFINED;
    }));
    globalEnv.register("+", JSObject.newFunction("+", (_1, _2, args) -> (Integer)args[0] + (Integer)args[1]));
    globalEnv.register("-", JSObject.newFunction("-", (_1, _2, args) -> (Integer)args[0] - (Integer)args[1]));
    globalEnv.register("/", JSObject.newFunction("/", (_1, _2, args) -> (Integer)args[0] / (Integer)args[1]));
    globalEnv.register("*", JSObject.newFunction("*", (_1, _2, args) -> (Integer)args[0] * (Integer)args[1]));
    globalEnv.register("%", JSObject.newFunction("%", (_1, _2, args) -> (Integer)args[0] * (Integer)args[1]));
    
    globalEnv.register("==", JSObject.newFunction("==", (_1, _2, args) -> args[0].equals(args[1])? 1: 0));
    globalEnv.register("!=", JSObject.newFunction("!=", (_1, _2, args) -> !args[0].equals(args[1])? 1: 0));
    globalEnv.register("<",  JSObject.newFunction("<",  (_1, _2, args) -> (((Comparable<Object>)args[0]).compareTo(args[1]) < 0)? 1: 0));
    globalEnv.register("<=", JSObject.newFunction("<=", (_1, _2, args) -> (((Comparable<Object>)args[0]).compareTo(args[1]) <= 0)? 1: 0));
    globalEnv.register(">",  JSObject.newFunction(">",  (_1, _2, args) -> (((Comparable<Object>)args[0]).compareTo(args[1]) > 0)? 1: 0));
    globalEnv.register(">=", JSObject.newFunction(">=", (_1, _2, args) -> (((Comparable<Object>)args[0]).compareTo(args[1]) >= 0)? 1: 0));
    
    JSObject function = Rewriter.createFunction(Optional.of("main"), Collections.emptyList(), body, new Dictionary(), globalEnv);
    function.invoke(null, new Object[0]);
  }
}
