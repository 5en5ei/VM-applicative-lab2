package fr.umlv.smalljs.stackinterp;

import static fr.umlv.smalljs.rt.JSObject.UNDEFINED;
import static fr.umlv.smalljs.stackinterp.Instructions.*;
import static fr.umlv.smalljs.stackinterp.TagValues.*;

import fr.umlv.smalljs.rt.JSObject;

public class Example {
  private static int[] topLevel(Dictionary dict, JSObject globalEnv) {
    return new int[]{
        // Q1
        /*
        CONST, encodeSmallInt(42),
        PRINT,
        POP,
        CONST, encodeObject(UNDEFINED, dict),
        RET,*/

        // Q2
        /*CONST, encodeSmallInt(3),
        DUP,
        REGISTER, encodeObject("a", dict),
        POP,
        LOOKUP, encodeObject("a", dict),
        PRINT,
        POP,
        CONST, encodeObject(UNDEFINED, dict),
        RET,*/

        // Q3
        /*CONST, encodeSmallInt(3),
        DUP,
        REGISTER, encodeObject("b", dict),
        DUP,
        REGISTER, encodeObject("a", dict),
        POP,

        LOOKUP, encodeObject("a", dict),
        PRINT,
        POP,

        LOOKUP, encodeObject("b", dict),
        PRINT,
        POP,

        CONST, encodeObject(UNDEFINED, dict),
        RET,*/


        CONST, encodeObject(newFunction("addTwo", new Code(new int[]{
            LOOKUP, encodeObject("+", dict),
            LOAD, 0,
            CONST, encodeSmallInt(2),
            FUNCALL, 2,
            RET,
        }, 1, 1), dict, globalEnv), dict),
        REGISTER, encodeObject("addTwo", dict),
        LOOKUP, encodeObject("addTwo", dict),
        CONST, encodeSmallInt(5),
        FUNCALL, 1,
        PRINT,
        POP,
        CONST, encodeObject(UNDEFINED, dict),
        RET
    };
  }

  private static JSObject newFunction(String name, Code code, Dictionary dict, JSObject globalEnv) {
    JSObject fun = JSObject.newFunction(name, (self, receiver, arguments) ->
        StackInterpreter.execute(self, dict, globalEnv)
    );
    fun.register("__code__", code);
    return fun;
  }

  public static void main(String[] args) {
    Dictionary dict = new Dictionary();
    JSObject globalEnv = JSObject.newEnv(null);
    globalEnv.register("global", globalEnv);
    globalEnv.register("+", JSObject.newFunction("+", (_1, _2, _args) -> (Integer)_args[0] + (Integer)_args[1]));

    Code mainCode = new Code(topLevel(dict, globalEnv), 0, 0);
    JSObject main = newFunction("main", mainCode, dict, globalEnv);

    main.invoke(null, new Object[0]);
  }
}
