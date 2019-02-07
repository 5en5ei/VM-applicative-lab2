package fr.umlv.smalljs.stackinterp;

public interface Instructions {
  int CONST = 1;            // CONST value
  int LOOKUP = 2;           // LOOKUP global_name
  int REGISTER = 3;         // REGISTER global_name
  int LOAD = 4;             // LOAD  slot_index
  int STORE = 5;            // STORE slot_index
  int DUP = 6;              
  int POP = 7;              
  int FUNCALL = 8;          // FUNCALL argument_count
  int RET = 9;
  int GOTO = 10;            // GOTO  instr_index
  int JUMP_IF_FALSE = 11;   // JUMP_IF_FALSE instr_index
  int JUMP_IF_TRUE = 12;    // JUMP_IF_TRUE instr_index
  
  int PRINT = 20;

  static void dump(int[] instrs, Dictionary dict) {
    String[] strings = {
      null, "CONST", "LOOKUP", "REGISTER", "LOAD", "STORE", "DUP", "POP", "FUNCALL", "RET", "GOTO", "JUMP_IF_FALSE", "JUMP_IF_TRUE",
      null, null, null, null, null, null, null, "PRINT"
    };
    for(int pc = 0; pc < instrs.length;) {
      System.out.print(pc + " ");
      int instr = instrs[pc++];
      switch(instr) {
      case DUP:   // no-arg instr
      case POP:
      case RET:
      case PRINT:
        System.out.println(strings[instr]);
        continue;
        
      case LOAD:   // int arg instr
      case STORE:
      case GOTO:
      case JUMP_IF_FALSE:
      case FUNCALL: {
        int operand = instrs[pc++];
        System.out.println(strings[instr] + " " +  operand);
        continue;
      }
      
      case CONST:   // dictionnary constant arg instr
      case LOOKUP:
      case REGISTER: {  
        int operand = instrs[pc++];
        System.out.println(strings[instr] + " " +  TagValues.decodeAnyValue(operand, dict));
        continue;
      }  
      default:
        throw new Error("unknown instr " +instr);
      }
    }
    System.out.println();
  }
}