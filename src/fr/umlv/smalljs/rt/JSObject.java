package fr.umlv.smalljs.rt;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class JSObject {
  private final JSObject proto;
  private final String name;
  private final Invoker invoker;
  private final LinkedHashMap<String, Object> valueMap = new LinkedHashMap<>();
  
  public static final Object UNDEFINED = new Object() {  @Override public String toString() { return "undefined"; }};
  
  public interface Invoker {
    Object invoke(JSObject self, Object receiver, Object[] args);
  }
  
  private JSObject(JSObject proto, String name, Invoker invoker) {
    this.proto = proto;
    this.name = requireNonNull(name);
    this.invoker = requireNonNull(invoker);
  }
  
  public static JSObject newObject(JSObject proto) {
    return new JSObject(proto, "object", (_1, _2, _3) -> { throw new Failure("object can not be applied"); });
  }
  public static JSObject newEnv(JSObject parent) {
    return new JSObject(parent, "env", (_1, _2, _3) -> { throw new Failure("env can not be applied"); });
  }
  public static JSObject newFunction(String name, Invoker invoker) {
    JSObject function = new JSObject(null, "function " + name, invoker);
    function.register("apply", function);
    return function;
  }
  
  public Object invoke(Object receiver, Object[] args) {
    return invoker.invoke(this, receiver, args);
  }
  
  public Object lookup(String key) {
    requireNonNull(key);
    Object value = valueMap.get(key);
    if (value != null) {
      return value;
    }
    if (proto != null) {
      return proto.lookup(key);
    }
    return UNDEFINED;
  }

  public void register(String key, Object value) {
    requireNonNull(key);
    requireNonNull(value);
    valueMap.put(key, value);
  }
  
  public int length() {
    return valueMap.size();
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    toString(this, builder, Collections.newSetFromMap(new IdentityHashMap<>()));
    return builder.toString();
  }

  private static void toString(Object object, StringBuilder builder, Set<Object> seen) {
    if(object == null) {
      builder.append("null");
      return;
    }
    if (!seen.add(object)) {
      builder.append("...");
      if (object instanceof JSObject) {
        builder.append(" // ").append(((JSObject)object).name);
      }
      return;
    }
    if (!(object instanceof JSObject)) {
      builder.append(object);
      return;
    }
    JSObject jsObject = (JSObject)object;
    builder.append("{ // ").append(jsObject.name).append('\n');
    jsObject.valueMap.forEach((key, value) -> {
      builder.append("  ").append(key).append(": ");
      toString(value, builder, seen);
      builder.append("\n");
    });
    builder.append("  proto: ");
    toString(jsObject.proto, builder, seen);
    builder.append("\n");
    builder.append("}");
  }
  
}