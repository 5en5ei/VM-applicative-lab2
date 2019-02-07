package fr.umlv.smalljs.main;

import static fr.umlv.smalljs.ast.ASTBuilder.createScript;
import static fr.umlv.smalljs.stackinterp.StackInterpreter.interpret;
import static java.lang.System.in;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import fr.umlv.smalljs.ast.Script;

public class Main {
  public static void main(String[] args) throws IOException {
    Script script;
    try(Reader reader = (args.length > 0)? newBufferedReader(get(args[0])): new InputStreamReader(in)) {
      script = createScript(reader);
    }
    interpret(script);
  }
}
